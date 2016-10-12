package ch.elca.elcathon.awsiotsample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

/**
 * Activity showing thing shadow interaction.
 */
public class IotActivity extends Activity {

    /*
     * Application configuration.
     * Make sure not to commit this!
     */
    private static final String REST_ENDPOINT = "REST ENDPOINT WITHOUT ANY /";
    private static final String COGNITO_POOL_ID = "COGNITO POOL ID";
    private static final Regions REGION = Regions.EU_CENTRAL_1;
    private static final String THING_NAME = "THING SHADOW NAME";

    /**
     * Credentials provider used as the mobile device is not considered confidential.
     */
    private CognitoCachingCredentialsProvider credentialsProvider;

    /**
     * AWS IoT API class.
     */
    private AWSIotDataClient iotDataClient;

    private TextView currentText;
    private TextView newText;

    private Button refreshButton;
    private Button updateButton;

    /**
     * JSON serializer/deserializer.
     */
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot);

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                REGION
        );

        iotDataClient = new AWSIotDataClient(credentialsProvider);
        iotDataClient.setEndpoint(REST_ENDPOINT);

        gson = new Gson();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentText = TextView.class.cast(findViewById(R.id.currentText));
        newText = TextView.class.cast(findViewById(R.id.newText));

        refreshButton = Button.class.cast(findViewById(R.id.refreshButton));
        updateButton = Button.class.cast(findViewById(R.id.updateButton));

        // refresh the current state
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetShadowTask task = new GetShadowTask();
                task.execute();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateShadowTask task = new UpdateShadowTask();
                task.execute(newText.getText().toString());
            }
        });
    }

    private class GetShadowTask extends AsyncTask<Void, Void, ThingState> {

        // TODO: error handling
        @Override
        protected ThingState doInBackground(Void... voids) {
            GetThingShadowRequest request = new GetThingShadowRequest().withThingName(THING_NAME);
            GetThingShadowResult result = iotDataClient.getThingShadow(request);

            byte[] bytes = new byte[result.getPayload().remaining()];
            result.getPayload().get(bytes);

            return gson.fromJson(new String(bytes), ThingState.class);
        }

        @Override
        protected void onPostExecute(ThingState state) {
            currentText.setText(state.state.reported.message);
        }
    }

    private class UpdateShadowTask extends AsyncTask<String, Void, Void> {

        // TODO: error handling
        @Override
        protected Void doInBackground(String... params) {
            UpdateThingShadowRequest request = new UpdateThingShadowRequest().withThingName(THING_NAME);

            String thingState = String.format("{\"state\":{\"desired\":{\"message\":\"%s\"}}}", params[0]);

            request.setPayload(ByteBuffer.wrap(thingState.getBytes()));

            iotDataClient.updateThingShadow(request);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // reset the typed text
            newText.setText("");
        }
    }

}
