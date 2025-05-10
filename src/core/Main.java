package core;

import javax.swing.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which user interface is closet to you?");
        System.out.println("1. Console Interface Gameplay");
        System.out.println("2. Graphical User Interface Gameplay");
        System.out.print("Enter 1 or 2: ");
        int t = 0;
        try {
            t = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
        }
        if (t == 1) {
            GameController gameController = new GameController();
            gameController.gameLoop();
        } else if (t == 2) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new SeaBattleGUI();
                }
            });
        } else {
            System.out.println("You have not entered a valid option!");
            System.exit(0);
        }
    }
}