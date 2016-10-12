# ELCAthon AWS IoT Android sample
The following *Android* sample project shows simple interactions with the ***thing shadow* API**. For additional examples see the [AWS SDK Android Samples GitHub repository](https://github.com/awslabs/aws-sdk-android-samples).

## Configuration
> **Caution: Make sure not to commit any AWS configuration.**

Create the things in the *AWS IoT Dashboard* and configure them in the `IoTActivity` if you wish to run this sample.

Make sure to setup [Amazon Cognito](https://github.com/aws/aws-sdk-android#create-an-amazon-cognito-identity-pool) appropriately before trying to run the example.

## Running
The example can be run either from within AndroidStudio or by issuing:

    gradlew installDebug
