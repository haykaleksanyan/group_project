public class BasicAttack extends Weapon {
    public BasicAttack() {
        super("Basic", 0);
    }

    public void use(Player attacker, Player defender, int row, int col) {
        defender.getBoard().receiveAttack(row, col);
    }

    public String toString() {
        return getName() + " (Free)";
    }

}
