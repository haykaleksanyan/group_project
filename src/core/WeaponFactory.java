package core;

import exceptions.*;

public class WeaponFactory {
    public static Weapon createWeapon(String typeName) throws IllegalWeaponTypeException {
        for (WeaponType wt : WeaponType.values()) {
            if (wt.getName().equalsIgnoreCase(typeName)) {
                switch (wt) {
                    case MISSILE_V:
                        return new Missile(true);  // Vertical core.Missile
                    case MISSILE_H:
                        return new Missile(false); // Horizontal core.Missile
                    case BOMB:
                        return new Bomb();
                    case BASIC:
                        return new BasicAttack();
                    default:
                        // This case should not be reached if all enum types are handled
                        throw new IllegalWeaponTypeException("Not defined weapon type in factory: " + typeName);
                }
            }
        }
        throw new IllegalWeaponTypeException("Invalid weapon type: " + typeName);
    }
}