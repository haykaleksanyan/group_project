package core;

import exceptions.AlreadyAttackedException;

public abstract class Weapon {
    private WeaponType weaponType;

    public Weapon(WeaponType wt) {
        this.weaponType = wt;
    }

    public String getName() {
        return weaponType.getName();
    }

    public String getDisplayName() {
        return weaponType.getDisplayName();
    }

    public int getPrice() {
        return weaponType.getPrice();
    }

    public abstract void use(Player attacker, Player defender, int x, int y) throws AlreadyAttackedException;

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Weapon weapon = (Weapon) other;
        return weaponType.getName().equalsIgnoreCase(weapon.getName());
    }

    public boolean equals(WeaponType other) {
        return weaponType.getName().equalsIgnoreCase(other.getName());
    }

    public String toString() {
        return getDisplayName() + " ($" + getPrice() + ")";
    }

}
