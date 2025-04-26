public class WeaponFactory {
    public static Weapon createWeapon(String type) {
        switch (type.toLowerCase()) {
            case "missilev":
                return new Missile(true);  // vertical
            case "missileh":
                return new Missile(false); // horizontal
            case "bomb":
                return new Bomb();
            default:
                return null;
        }
    }
}
