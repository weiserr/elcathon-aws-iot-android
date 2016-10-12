package ch.elca.elcathon.awsiotsample;

/**
 * Rudimentary DTO class for the thing state.
 */
public class ThingState {
    State state;

    class State {
        Desired desired;
        Reported reported;

        class Desired {
            String message;
        }

        class Reported {
            String message;
        }
    }
}
