package core;

import exceptions.AlreadyAttackedException;

public class Missile extends Weapon {
    private boolean isVertical;

    public Missile(boolean isVertical) {
        super(isVertical ? WeaponType.MISSILE_V : WeaponType.MISSILE_H);
        this.isVertical = isVertical;
    }

    public void use(Player attacker, Player defender, int row, int col) throws AlreadyAttackedException {
        if (isVertical) {
            for (int i = 0; i < Board.BOARD_SIZE; i++) {
                if (!defender.getBoard().getCellAt(i, col).isHit()) {
                    attacker.basicAttack(defender, i, col);
                }
            }
        } else {
            for (int i = 0; i < Board.BOARD_SIZE; i++) {
                if (!defender.getBoard().getCellAt(row, i).isHit()) {
                    attacker.basicAttack(defender, row, i);
                }
            }
        }
    }
}
