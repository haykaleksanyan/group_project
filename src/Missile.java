public class Missile extends Weapon {
    private boolean isVertical;

    public Missile(boolean isVertical) {
        String n = "MissileH";
        if (isVertical) {
            n = "MissileV";
        }
        super(n, 70);
        this.isVertical = isVertical;
    }

    public void use(Player attacker, Player defender, int row, int col) {
        if (isVertical) {
            for (int i = 0; i < Board.BOARD_SIZE; i++) {
                attacker.basicAttack(defender, i, col);
            }
        } else {
            for (int i = 0; i < Board.BOARD_SIZE; i++) {
                attacker.basicAttack(defender, row, i);
            }
        }
    }

    public String toString() {
        return getName() + " (" + (isVertical ? "Vertical" : "Horizontal") + ", 70 coins)";
    }
}
