public class Main {
    public static void main(String[] args) {
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");
        player1.selfBoard.fillRandom();
        player2.selfBoard.fillRandom();
    }
}