public class Cell {
    private boolean hasShip;
    private boolean isHit;

    public boolean isOccupied() {
        return hasShip;
    }

    public boolean isHit() {
        return isHit;
    }

    public void placeShip() {
        hasShip = true;
    }

    public boolean attack() {
        if (hasShip && !isHit) {
            isHit = true;
            return true;
        }
        return false;
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
