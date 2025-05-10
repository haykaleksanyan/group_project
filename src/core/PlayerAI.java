package core;

import exceptions.AlreadyAttackedException;
import exceptions.IllegalWeaponTypeException;
import exceptions.InvalidPlacementException;
import fileIO.FileLogger;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;

public class PlayerAI extends Player {

    private Random random;

    public PlayerAI(String name, int budget) {
        super(name, budget);
        this.random = new Random();
    }

    public void placeShipsAutomatically() {
        System.out.println(getName() + " is placing ships automatically...");
        int shipsToPlace = Board.SHIPS_COUNT;
        int attempts = 0;
        int maxAttemptsPerShip = Board.BOARD_SIZE * Board.BOARD_SIZE * 2;

        while (getShipsCount() < shipsToPlace && attempts < maxAttemptsPerShip * shipsToPlace) {
            int r = random.nextInt(Board.BOARD_SIZE);
            int c = random.nextInt(Board.BOARD_SIZE);
            try {
                if (getBoard().canBePlacedAt(r, c)) {
                    placeShip(r, c);
                    System.out.println(getName() + " placed a ship at (" + r + "," + c + "). Ships: " + getShipsCount() + "/" + shipsToPlace);
                    attempts = 0;
                }
            } catch (InvalidPlacementException e) {
                System.out.println(getName() + " auto-placement error: " + e.getMessage());
                FileLogger.error(getName() + " auto-placement error: " + e.getMessage());
            }
            attempts++;
        }
        if (getShipsCount() < shipsToPlace) {
            System.err.println(getName() + " could not place all ships automatically after many attempts.");
        }
        System.out.println(getName() + " finished placing ships automatically.");
    }

    public void purchaseWeaponsAutomatically() {
        System.out.println(getName() + " is purchasing weapons automatically...");

        for (WeaponType wt : WeaponType.values()) {
            if (wt != WeaponType.BASIC) {
                if (getBudget() >= wt.getPrice()) {
                    try {
                        if (buyWeapon(wt.getName())) {
                            System.out.println(getName() + " purchased " + wt.getDisplayName());
                        }
                    } catch (IllegalWeaponTypeException e) {
                        System.err.println(getName() + " auto-purchase error: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println(getName() + " finished purchasing weapons. Budget: $" + getBudget() + ", Weapons: " + getWeaponCount());
    }

    public Point selectAttackTarget(Board opponentBoard) {
        ArrayList<Point> availableTargets = new ArrayList<>();
        for (int r = 0; r < Board.BOARD_SIZE; r++) {
            for (int c = 0; c < Board.BOARD_SIZE; c++) {
                if (!opponentBoard.getCellAt(r, c).isHit()) {
                    availableTargets.add(new Point(r, c));
                }
            }
        }

        if (availableTargets.isEmpty()) {
            return null;
        }
        return availableTargets.get(random.nextInt(availableTargets.size()));
    }

    public void performAttackAutomatically(Player opponent) {
        System.out.println(getName() + " is attacking " + opponent.getName() + " automatically...");

        Point target = selectAttackTarget(opponent.getBoard());
        if (target == null) {
            System.out.println(getName() + " found no valid targets to attack.");
            return;
        }

        String weaponToUse = WeaponType.BASIC.getName();
        ArrayList<Weapon> inventory = getInventory();

        if (!inventory.isEmpty()) {
            weaponToUse = inventory.get(random.nextInt(inventory.size())).getName();
        }

        System.out.println(getName() + " chose to use " + weaponToUse + " at (" + target.x + "," + target.y + ")");

        try {
            useWeapon(weaponToUse, opponent, target.x, target.y);
        } catch (IllegalWeaponTypeException | AlreadyAttackedException e) {
            System.err.println(getName() + " auto-attack error: " + e.getMessage());
            if (e instanceof IllegalWeaponTypeException && !weaponToUse.equals(WeaponType.BASIC.getName())) {
                System.out.println(getName() + " attempting fallback to Basic Attack due to weapon issue.");
                try {
                    useWeapon(WeaponType.BASIC.getName(), opponent, target.x, target.y);
                } catch (Exception fallbackEx) {
                    System.err.println(getName() + " fallback Basic Attack error: " + fallbackEx.getMessage());
                }
            }
        }
    }
}