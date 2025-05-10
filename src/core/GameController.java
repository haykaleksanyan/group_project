package core;

import fileIO.FileLogger;
import exceptions.*;

import java.util.Scanner;

public class GameController {

    private Player currentAttacker;
    private Player currentDefender;
    private Player player1;
    private Player player2;
    private Scanner scanner;

    public GameController() {
        scanner = new Scanner(System.in);
    }

    private void startGame() {
        System.out.println("Welcome to the Battle of Ships game!");
        setupPlayers();
        placeShips(player1);
        placeShips(player2);
        purchaseWeapons(player1);
        purchaseWeapons(player2);
    }

    private void setupPlayers() {
        int defaultBudget = 150;
        System.out.print("Please enter player1 name: ");
        String player1Name = scanner.nextLine();
        System.out.print("Please enter player2 name: ");
        String player2Name = scanner.nextLine();
        player1 = new Player(player1Name, defaultBudget);
        player2 = new Player(player2Name, defaultBudget);
        currentAttacker = player1;
        currentDefender = player2;
        FileLogger.history("Game started between: " + player1.getName() + " and " + player2.getName());
        logBoard(player1.getBoard(), true);
        logBoard(player2.getBoard(), true);
    }

    private void placeShips(Player player) {
        System.out.println(player.getName() + " in order to place a ship");
        while (player.getShipsCount() < Board.SHIPS_COUNT) {
            player.getBoard().display(true);
            System.out.print("Enter the coordinates x and y (e.g. 0 0): ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            try {
                if (player.placeShip(row, col)) {
                    System.out.println("Ship placed, " + Board.SHIPS_COUNT + "/" + player.getShipsCount() + " ships placed");
                }
            } catch (InvalidPlacementException e) {
                System.out.println(e.getMessage());
                FileLogger.error(e.getMessage());
            }

        }
        FileLogger.history(player.getName() + "'s ships placed:");
    }


    private void logBoard(Board board, boolean revealShips) {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            String line = "[ ";
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                line += board.getCellAt(i, j).toString() + " ";
            }
            line += "]";
            FileLogger.history(line);
        }
    }


    private void purchaseWeapons(Player player) {
        Missile missileV = new Missile(true);
        Missile missileH = new Missile(false);
        Bomb bomb = new Bomb();

        System.out.println(player.getName() + ", time to purchase your weapons!");
        System.out.println("To buy a " + missileV.toString() + ", type \"MissileV\"");
        System.out.println("To buy a " + missileH.toString() + ", type \"MissileH\"");
        System.out.println("To buy a " + bomb.toString() + ", type \"core.Bomb\"");

        while (player.getBudget() >= 60) {
            System.out.println("You currently have $" + player.getBudget());

            int i = player.getWeaponCount();
            scanner.nextLine();

            System.out.print("Type " + (i + 1) + " weapon name, or type \"L\" to leave:");
            String wName = scanner.nextLine();
            if (wName.equals("L")) {
                break;
            }
            try {
                if (player.buyWeapon(wName)) {
                    System.out.println("You purchased a " + wName);
                }
            } catch (IllegalWeaponTypeException e) {
                System.out.println(e.getMessage());
                FileLogger.error(e.getMessage());
            }
        }
        FileLogger.history(player.getName() + "'s weapons:");
        for (int i = 0; i < player.getWeaponCount(); i++) {
            FileLogger.history("\t- " + player.getInventory().get(i).toString());
        }
    }

    private void performTurn() {
        while (true) {
            System.out.println("\n----------------\n");
            System.out.println(currentAttacker.getName() + ", you will now attack!");
            System.out.println("Currently " + currentDefender.getName() + "'s board is this");
            currentDefender.getBoard().display(false);
            System.out.println(currentAttacker.getName() + " choose a weapon type, and the position");
            System.out.print("Type \"MissileV\", \"MissileH\", \"core.Bomb\" or anything else for basic attack:");
            String wName = scanner.next().trim();
            System.out.print("Type the coordinates (e.g. 0 0): ");

            int row = scanner.nextInt();
            int col = scanner.nextInt();

            try {
                wName = wName.toUpperCase();
                currentAttacker.useWeapon(wName, currentDefender, row, col);
                FileLogger.history(currentAttacker.getName() + " used " + wName + " at (" + row + ", " + col + ")");
                break;
            } catch (AlreadyAttackedException e) {
                System.out.println(e.getMessage());
                FileLogger.error(e.getMessage());
                System.out.println("Already attacked position, try again");
            } catch (IllegalWeaponTypeException e) {
                System.out.println("Invalid weapon type! Using default Basic Attack.");
                FileLogger.error("Invalid weapon type: " + wName + ". Causing to use default Basic Attack.");
                break;
            }
        }
        System.out.println("You attacked successfully, (" + currentAttacker.getWeaponCount() + " weapons left)");
        currentDefender.getBoard().display(false);

        if (currentDefender.checkDefeat()) {
            System.out.println(currentAttacker.getName() + " won the game! Congratulations!");
            FileLogger.history(currentAttacker.getName() + " won the game!");
            System.exit(0);
        }
        Player p = currentAttacker;
        currentAttacker = currentDefender;
        currentDefender = p;
        System.out.println("Turn changed!");
    }

    public void gameLoop() {
        startGame();
        while (true) {
            performTurn();
        }
    }
}
