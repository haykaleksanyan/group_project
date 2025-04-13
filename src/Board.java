import java.util.Random;

public class Board {
    public static final int BOARD_SIZE = 10;
    public static final int SHIP_NUMBER = 15;

    private Ship[][] board;
    private String name;
    public int currentShipsNumber;

    public Board(String name) {
        this.name = name;
        board = new Ship[BOARD_SIZE][BOARD_SIZE];
        currentShipsNumber = 0;
        // All cells are null by default, representing water
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = null;
            }
        }
    }

    public void fillRandom() {
        Random rand = new Random();
        while (currentShipsNumber < SHIP_NUMBER) {
            int row = rand.nextInt(BOARD_SIZE);
            int col = rand.nextInt(BOARD_SIZE);
            Ship ship = new Ship(1, row, col, true);
            if(placeShip(ship)){
                incrementShipsNumber();
            }
        }
    }

    public void incrementShipsNumber() {
        currentShipsNumber++;
    }

    public void placeHit(int row, int col) {
        if (isOccupied(row, col)) {
            if (!getShipAt(row, col).isHit(row, col)) {
                getShipAt(row, col).registerHit(row, col);
            }
        }
    }


    public boolean placeShip(Ship ship) {
        int row = ship.getStartRow();
        int col = ship.getStartCol();
        if (!isOccupied(row, col)) {
            board[row][col] = ship;
            return true;
        }
        return false;
    }

    public Ship getShipAt(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return null;
        }
        return board[row][col];
    }

    public boolean isOccupied(int row, int col) {
        return getShipAt(row, col) != null;
    }

    public String getName() {
        return name;
    }


    // This may change to show hit and passive state
    public void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    System.out.print("O ");
                } else {
                    System.out.print("~ ");
                }
            }
            System.out.println();
        }
    }
}