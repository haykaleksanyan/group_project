package exceptions;

public class InvalidPlacementException extends Exception {
    public InvalidPlacementException(String message) {
        super(message);
    }

    public InvalidPlacementException() {
        super("Error: Invalid Placement Exception");
    }
}

