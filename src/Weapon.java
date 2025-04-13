public abstract class Weapon {
    public String weaponName;
    public int price;

    public Weapon(String name, int price) {
        weaponName = name;
        this.price = price;
    }


    public abstract void fire(Board enemyBoard, int row, int col, );


}
