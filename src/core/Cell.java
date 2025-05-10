package core;
//import exception
import exceptions.*;

public class Cell {
    private boolean hasShip;
    private boolean isHit;

    public Cell(boolean hasShip, boolean isHit) {
        this.hasShip = hasShip;
        this.isHit = isHit;
    }

    public Cell() {
        this(false, false);
    }

    public boolean isOccupied() {
        return hasShip;
    }

    public boolean isHit() {
        return isHit;
    }

    public boolean isSunk(){
        return isHit && hasShip;
    }

    public void placeShip() {
        hasShip = true;
    }

    public boolean attack() throws AlreadyAttackedException {
        if (isHit) {
            throw new AlreadyAttackedException("core.Cell is already attacked");
        }
        isHit = true;
        return hasShip;
    }


    public String toString(boolean reveal) {
        String value = "~";
        if (isHit) {
            value = "o";
        }
        if (isHit && hasShip) {
            value = "x";
        }
        if (reveal && hasShip && !isHit) {
            value = "□";
        }
        return value;
    }

    public String toString() {
        return toString(false);
    }


}
