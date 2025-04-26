public class Bomb extends Weapon {
    public Bomb() {
        super("Bomb", 50);
    }

    public void use(Player attacker, Player defender, int row, int col) {
        int startRow = Math.max(0, row - 1);
        int endRow = Math.min(Board.BOARD_SIZE - 1, row + 1);
        int startCol = Math.max(0, col - 1);
        int endCol = Math.min(Board.BOARD_SIZE - 1, col + 1);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                attacker.basicAttack(defender, i, j);
            }
        }

    }
}