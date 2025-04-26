import java.util.ArrayList;

public class Player {
    private String name;
    private Board board;
    private int budget;
    private ArrayList<Ship> ships;
    private Weapon basicWeapon;
    private ArrayList<Weapon> inventory;

    public Player(String name, int budget) {
        this.board = new Board();
        this.ships = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.name = name;
        this.budget = budget;
        basicWeapon = new BasicAttack();
    }

    public boolean placeShip(int row, int col) {
        if (board.placeShip(row, col)) {
            Ship ship = new Ship(row, col);
            ships.add(ship);
            return true;
        }
        return false;
    }

    public boolean buyWeapon(String weaponName) {
        Weapon weapon = WeaponFactory.createWeapon(weaponName);
        if (budget >= weapon.getPrice()) {
            budget -= weapon.getPrice();
            inventory.add(weapon);
            return true;
        }
        return false;
    }

    public boolean useWeapon(String weaponName, Player opponent, int row, int col) {
        boolean hasWeapon = false;
        int indexOfWeapon = 0;
        for (Weapon w : inventory) {
            if (w.equals(weaponName)) {
                hasWeapon = true;
                indexOfWeapon = inventory.indexOf(w);
                break;
            }
        }
        if (hasWeapon) {
            inventory.get(indexOfWeapon).use(this, opponent, row, col);
            inventory.remove(indexOfWeapon);
            return true;
        }
        return false;
    }

    public boolean basicAttack(Player opponent, int row, int col) {
        basicWeapon.use(this, opponent, row, col);
        return true;
    }

    public Board getBoard() {
        return board;
    }

    public int getRemainingShips(){
        return ships.size();
    }

}
