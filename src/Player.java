public class Player {
    public Board selfBoard;
    public Board opponentBoard;
    public String nickname;
    public boolean win;
    public Coordinate[] hitsOnOpponent;

    public Player(String nickname) {
        selfBoard = new Board("Player board");
        opponentBoard = new Board("Opponent's board");
        this.nickname = nickname;
        win = false;
        hitsOnOpponent = new Coordinate[Board.BOARD_SIZE * Board.BOARD_SIZE];
    }
    public void registerHit(int col, int row){

    }
}
