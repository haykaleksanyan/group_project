public abstract class Weapon {
    private String name;
    private int price;

    public Weapon(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public abstract void use(Player attacker, Player defender, int x, int y);

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Weapon weapon = (Weapon) other;
        return name.equalsIgnoreCase(weapon.name);
    }

    public boolean equals(String otherName) {
        return name.equalsIgnoreCase(otherName);
    }

    public String toString() {
        return name + " (" + price + " coins)";
    }

}
