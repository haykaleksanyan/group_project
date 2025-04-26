public class Board {
    public static final int BOARD_SIZE = 10;

    private Cell[][] grid;

    public Board() {
        int size = BOARD_SIZE;
        grid = new Cell[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = new Cell();
            }
        }
    }

    public boolean placeShip(int row, int col) {
        if (canBePlacedAt(row, col)) {
            grid[row][col].placeShip();
            return true;
        }
        return false;
    }

    public boolean receiveAttack(int row, int col) {
        return grid[row][col].attack();
    }

    public void display(boolean revealShips) {
        int size = BOARD_SIZE;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid[i][j];
                System.out.print(cell.toString(revealShips) + " | ");
            }
            System.out.println();
        }
    }

    private boolean canBePlacedAt(int row, int col) {
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
