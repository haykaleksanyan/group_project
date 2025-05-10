package exceptions;

public class IllegalWeaponTypeException extends Exception {
    public IllegalWeaponTypeException(String message) {
        super(message);
    }
    public IllegalWeaponTypeException() {
        super("Error: Illegal Argument Exception");
    }
}