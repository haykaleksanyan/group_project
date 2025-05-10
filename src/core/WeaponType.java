package core;

public enum WeaponType {
    MISSILE_V("MissileV", 70, "Vertical Missile"),
    MISSILE_H("MissileH", 70, "Horizontal Missile"),
    BOMB("Bomb", 60, "Bomb"),
    BASIC("Basic", 0, "Basic Attack");

    private final String name;
    private final String displayName;
    private final int price;

    WeaponType(String name, int price, String displayName) {
        this.name = name;
        this.price = price;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }
}
