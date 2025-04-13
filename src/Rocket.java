public class Rocket extends Weapon {
    public boolean isVertical;

    public Rocket(String name, int price, boolean vertical) {
        super(name, price);
        isVertical = vertical;
    }

    public void fire(Board enemyBoard, int row, int col) {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (isVertical) {
                enemyBoard.placeHit(i, col);
            } else {
                enemyBoard.placeHit(row, i);
            }
        }
    }

}
