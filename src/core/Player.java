package core;

import java.util.ArrayList;

// importing the exceptins
import exceptions.*;

public class Player {
    private String name;
    private Board board;
    private int budget;
    private int shipsCount;
    private Weapon basicWeapon;
    private ArrayList<Weapon> inventory;
    public static final int INITIAL_BUDGET = 300;

    public Player(String name, int budget) {
        this.board = new Board();
        this.shipsCount = 0;
        this.inventory = new ArrayList<>();
        this.name = name;
        this.budget = budget;
        basicWeapon = new BasicAttack();
    }

    public boolean placeShip(int row, int col) throws InvalidPlacementException {
        if (board.placeShip(row, col)) {
            shipsCount++;
            return true;
        }
        return false;
    }

    public boolean buyWeapon(String weaponName) throws IllegalWeaponTypeException {
        Weapon weapon = WeaponFactory.createWeapon(weaponName);
        if (weapon != null && budget >= weapon.getPrice()) {
            budget -= weapon.getPrice();
            inventory.add(weapon);
            return true;
        }
        throw new IllegalWeaponTypeException("Cannot purchase weapon: " + weaponName);
    }

    public void useWeapon(String weaponName, Player opponent, int row, int col) throws IllegalWeaponTypeException, AlreadyAttackedException {

        for (int i = 0; i < inventory.size(); i++) {
            Weapon w = inventory.get(i);
            if (w.getName().equalsIgnoreCase(weaponName)) {
                w.use(this, opponent, row, col);
                inventory.remove(i);
                return;
            }
        }
        if (!weaponName.equalsIgnoreCase(WeaponType.BASIC.getName())) {
            throw new IllegalWeaponTypeException("core.Weapon not found: " + weaponName);
        }

        basicAttack(opponent, row, col);

    }

    public ArrayList<Weapon> getInventory() {
        return inventory;
    }

    public boolean checkDefeat() {
        return getShipsCount() <= 0;
    }

    public void basicAttack(Player opponent, int row, int col) throws AlreadyAttackedException {
        basicWeapon.use(this, opponent, row, col);
    }

    public Board getBoard() {
        return board;
    }

    public int getShipsCount() {
        return shipsCount;
    }

    public void reduceShipsCount() {
        shipsCount--;
    }

    public String getName() {
        return name;
    }

    public int getBudget() {
        return budget;
    }

    public int getWeaponCount() {
        return inventory.size();
    }
}
