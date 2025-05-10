package core;

import exceptions.*;
import fileIO.FileLogger;

public class BasicAttack extends Weapon {
    public BasicAttack() {
        super(WeaponType.BASIC);
    }

    public void use(Player attacker, Player defender, int row, int col) {
        try {
            boolean hit = defender.getBoard().receiveAttack(row, col);
            if (hit) {
                defender.reduceShipsCount();
            }
            FileLogger.history(attacker.getName() + " used Basic Attack on (" + row + ", " + col + ") → " + (hit ? "HIT" : "MISS"));
        } catch (AlreadyAttackedException e) {
            FileLogger.error(attacker.getName() + " tried to attack (" + row + ", " + col + ") again. " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public String toString() {
        return getDisplayName() + " (free)";
    }
}
