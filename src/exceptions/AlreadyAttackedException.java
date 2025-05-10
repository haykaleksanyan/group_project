package exceptions;

public class AlreadyAttackedException extends Exception {
    public AlreadyAttackedException(String message) {
        super(message);
    }
    public AlreadyAttackedException() {
        super("Error: Already Attacked Exception");
    }
}