public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    public void start();
    private void setupPlayers();
    private void setupBoards();
    private void purchaseWeapons(Player player);
    private void gameLoop();
    private void switchTurn();
}
