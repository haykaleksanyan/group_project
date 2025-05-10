package core;//import exceptions


public class Board {
    public static final int BOARD_SIZE = 10;
    public static final int SHIPS_COUNT = 10;

    private Cell[][] grid;

    public Board() {
        int size = BOARD_SIZE;
        grid = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public boolean placeShip(int row, int col) throws InvalidPlacementException {
        if (!canBePlacedAt(row, col)) {
            throw new InvalidPlacementException("Invalid placement at (" + row + ", " + col + ")");
        }
        grid[row][col].placeShip();
        return true;
    }


    public boolean receiveAttack(int row, int col) throws AlreadyAttackedException {
        return grid[row][col].attack();
    }

    public void display(boolean revealShips) {
        int size = BOARD_SIZE;

        for (int j = 0; j < size; j++) {
            System.out.printf("%4s", j);
        }

        for (int i = 0; i < size; i++) {
            System.out.println();
            System.out.print(i);
            for (int j = 0; j < size; j++) {
                Cell cell = grid[i][j];
                System.out.printf("| %1s ", cell.toString(revealShips));
            }
            System.out.println("|");

            System.out.print("--");
            for (int j = 0; j < size; j++) {
                System.out.print("----");
            }
        }

        System.out.println("-");
    }

    public Cell getCellAt(int row, int col) {
        return grid[row][col];
    }

    public boolean canBePlacedAt(int row, int col) {
        if (grid[row][col].isOccupied()) {
            return false;
        }

        int size = BOARD_SIZE;

        // All possible 8 neighbors are with these coordinates
        int[] rowPossiblePositions = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colPossiblePositions = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int k = 0; k < 8; k++) {
            int neighborRow = row + rowPossiblePositions[k];
            int neighborCol = col + colPossiblePositions[k];

            if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
                if (grid[neighborRow][neighborCol].isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }
}
