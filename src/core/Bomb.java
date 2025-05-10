package core;

import exceptions.AlreadyAttackedException;

public class Bomb extends Weapon {
    public Bomb() {
        super(WeaponType.BOMB);
    }

    public void use(Player attacker, Player defender, int row, int col) throws AlreadyAttackedException {
        int startRow = Math.max(0, row - 1);
        int endRow = Math.min(Board.BOARD_SIZE - 1, row + 1);
        int startCol = Math.max(0, col - 1);
        int endCol = Math.min(Board.BOARD_SIZE - 1, col + 1);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (!defender.getBoard().getCellAt(i, j).isHit()) {
                    attacker.basicAttack(defender, i, j);
                }
            }
        }
    }
}
