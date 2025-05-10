package core;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import exceptions.*;

// FileLogger (ensure it's configured if used)
import fileIO.FileLogger;


public class SeaBattleGUI extends JFrame {

    // Constants for the game
    private static final int GRID_SIZE = Board.BOARD_SIZE;
    private static final String WINDOW_TITLE = "Battle of the Sea";
    private static final Color WATER_COLOR_FALLBACK = new Color(173, 216, 230);
    private static final Color BORDER_COLOR = new Color(48, 131, 159);
    private static final int CELL_SIZE = 45;


    // ImageIcons for game assets
    private ImageIcon backgroundIcon;
    private ImageIcon ship1Icon, ship2Icon;
    private ImageIcon ship1SunkIcon, ship2SunkIcon, splashIcon;


    // GUI Components
    private ImagePanel player1GridPanel;
    private ImagePanel player2GridPanel;
    private JButton[][] player1Buttons;
    private JButton[][] player2Buttons;
    private JTextArea messageArea;
    private JLabel statusLabel;
    private JButton confirmButton;
    private JToggleButton revealShipsButton;

    private JPanel attackerWeaponPanel;
    private ButtonGroup weaponButtonGroup;


    // Game State and Logic
    private Player player1;
    private Player player2;
    private Player currentAttacker;
    private Player currentDefender;
    private GamePhase currentPhase;
    private boolean showCurrentPlayerShipsHint = false;
    private boolean isVsAI = false;


    // Using Java AWT Point
    private ArrayList<Point> tempSelectedShipCells;

    private enum GamePhase {
        INITIALIZING,
        PLAYER1_NAME_INPUT, PLAYER2_NAME_INPUT,
        PLAYER1_SHIP_PLACEMENT, PLAYER2_SHIP_PLACEMENT,
        PRE_PURCHASE_SHIP_HIDING,
        PLAYER1_WEAPON_PURCHASE, PLAYER2_WEAPON_PURCHASE,
        START_ATTACK_PROMPT,
        PLAYER1_TURN, PLAYER2_TURN,
        AI_MAKING_MOVE, GAME_MODE_SELECTION, GAME_OVER
    }

    public SeaBattleGUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255));

        loadImages();

        player1Buttons = new JButton[GRID_SIZE][GRID_SIZE];
        player2Buttons = new JButton[GRID_SIZE][GRID_SIZE];
        tempSelectedShipCells = new ArrayList<>();

        setupGUIComponents();

        currentPhase = GamePhase.INITIALIZING;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                advancePhase();
            }
        });
    }

    private void loadImages() {
        try {
            backgroundIcon = loadImageIcon("/images/background1.png", "Background", -1, -1);
            ship1Icon = loadImageIcon("/images/ship1.png", "Player 1 Ship", CELL_SIZE, CELL_SIZE);
            ship2Icon = loadImageIcon("/images/ship2.png", "Player 2 Ship", CELL_SIZE, CELL_SIZE);
            ship1SunkIcon = loadImageIcon("/images/ship1_sunk.gif", "Player 1 Sunk Ship", CELL_SIZE, CELL_SIZE);
            ship2SunkIcon = loadImageIcon("/images/ship2_sunk.gif", "Player 2 Sunk Ship", CELL_SIZE, CELL_SIZE);
            splashIcon = loadImageIcon("/images/splash.png", "Splash", CELL_SIZE, CELL_SIZE);
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            logMessage("Error loading one or more images. Game may not display correctly.");
        }
    }

    private ImageIcon loadImageIcon(String path, String description, int width, int height) {
        URL imgURL = this.getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL, description);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT); // Use SCALE_DEFAULT for GIFs
            return new ImageIcon(scaledImage, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    private void setupGUIComponents() {
        statusLabel = new JLabel("Welcome to Battle of the Sea!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(statusLabel, BorderLayout.NORTH);

        JPanel gridsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        gridsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        player1GridPanel = new ImagePanel(backgroundIcon);
        player1GridPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        player2GridPanel = new ImagePanel(backgroundIcon);
        player2GridPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        createGridButtons(player1GridPanel, player1Buttons, "Player 1's Waters");
        createGridButtons(player2GridPanel, player2Buttons, "Player 2's Waters");

        gridsPanel.add(player1GridPanel);
        gridsPanel.add(player2GridPanel);
        add(gridsPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        messageArea = new JTextArea(6, 30);
        messageArea.setEditable(false);
        messageArea.setBorder(BorderFactory.createTitledBorder("Game Log"));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        southPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mainControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        confirmButton = new JButton("Start Game");
        mainControlPanel.add(confirmButton);

        revealShipsButton = new JToggleButton("Reveal My Ships");
        revealShipsButton.setVisible(false);
        revealShipsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCurrentPlayerShipsHint = revealShipsButton.isSelected();
                refreshGuiGrids();
            }
        });

        mainControlPanel.add(revealShipsButton);

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        mainControlPanel.add(resetButton);
        southPanel.add(mainControlPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        attackerWeaponPanel = new JPanel();
        attackerWeaponPanel.setLayout(new BoxLayout(attackerWeaponPanel, BoxLayout.Y_AXIS));
        attackerWeaponPanel.setBorder(BorderFactory.createTitledBorder("Your Weapons"));
        attackerWeaponPanel.setVisible(false);
        weaponButtonGroup = new ButtonGroup();
        add(attackerWeaponPanel, BorderLayout.EAST);

        pack();
        setMinimumSize(new Dimension(850, 650));
        setVisible(true);
    }

    private void createGridButtons(ImagePanel parentPanel, JButton[][] buttons, String title) {
        parentPanel.setBorder(BorderFactory.createTitledBorder(title));
        Border cellBorder = BorderFactory.createLineBorder(BORDER_COLOR, 1);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(true);
                buttons[i][j].setBorder(cellBorder);
                buttons[i][j].setEnabled(false);
                parentPanel.add(buttons[i][j]);
            }
        }
    }

    private void setConfirmButtonAction(String text, ActionListener action) {
        confirmButton.setText(text);
        for (ActionListener al : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(al);
        }
        confirmButton.addActionListener(action);
        confirmButton.setEnabled(true);
        confirmButton.setVisible(true);
    }


    private void advancePhase() {
        revealShipsButton.setVisible(false);
        showCurrentPlayerShipsHint = false;
        revealShipsButton.setSelected(false);
        switch (currentPhase) {
            case INITIALIZING:
                currentPhase = GamePhase.GAME_MODE_SELECTION;
                promptGameMode();
                break;
            case GAME_MODE_SELECTION: // After mode is selected
                currentPhase = GamePhase.PLAYER1_NAME_INPUT;
                statusLabel.setText("Setting up Player 1...");
                confirmButton.setVisible(false); // Name prompt dialog will advance
                promptForPlayerName(1);
                break;
            case PLAYER1_NAME_INPUT:
                if (isVsAI) {
                    int initialBudget = Player.INITIAL_BUDGET;
                    player2 = new PlayerAI("AI Player", initialBudget);
                    logMessage("Player 2 is " + player2.getName());
                    currentPhase = GamePhase.PLAYER2_NAME_INPUT; // Mark as P2 name "obtained"
                    advancePhase();
                } else {
                    currentPhase = GamePhase.PLAYER2_NAME_INPUT;
                    statusLabel.setText("Setting up Player 2...");
                    confirmButton.setVisible(false);
                    promptForPlayerName(2);
                }
                break;
            case PLAYER2_NAME_INPUT:
                logMessage("Players initialized: " + player1.getName() + " and " + player2.getName());
                FileLogger.history("Game started between: " + player1.getName() + " and " + player2.getName());
                logBoardToFile(player1.getBoard(), true, player1.getName() + "'s initial board:");
                logBoardToFile(player2.getBoard(), true, player2.getName() + "'s initial board:");
                currentAttacker = player1;
                currentDefender = player2;
                currentPhase = GamePhase.PLAYER1_SHIP_PLACEMENT;
                prepareShipPlacement();
                break;
            case PLAYER1_SHIP_PLACEMENT:
                break;
            case PLAYER2_SHIP_PLACEMENT:
                break;
            case PRE_PURCHASE_SHIP_HIDING:
                statusLabel.setText("Ship placement complete. Hiding boards before weapon purchase.");
                setConfirmButtonAction("Continue to Weapon Purchase", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        advancePhase();
                    }
                });
                refreshGuiGrids();
                currentAttacker = player1;
                currentDefender = player2;
                currentPhase = GamePhase.PLAYER1_WEAPON_PURCHASE;
                break;
            case PLAYER1_WEAPON_PURCHASE:
                prepareWeaponPurchase();
                break;
            case PLAYER2_WEAPON_PURCHASE:
                if (isVsAI && player2 instanceof PlayerAI) {
                    handleAIPurchase();
                } else {
                    prepareWeaponPurchase();
                }
                break;
            case START_ATTACK_PROMPT:
                statusLabel.setText("All players ready. Click 'Start Attack Phase' to begin!");
                setConfirmButtonAction("Start Attack Phase", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        advancePhase();
                    }
                });
                attackerWeaponPanel.setVisible(false);
                currentAttacker = player1;
                currentDefender = player2;
                currentPhase = GamePhase.PLAYER1_TURN;
                break;
            case PLAYER1_TURN:
                prepareAttackTurn();
                break;
            case PLAYER2_TURN:
                if (isVsAI && currentAttacker == player2 && player2 instanceof PlayerAI) {
                    handleAIAttack();
                } else {
                    prepareAttackTurn();
                }
                break;
            case AI_MAKING_MOVE:
                if (isVsAI && currentAttacker == player2 && player2 instanceof PlayerAI) {
                    handleAIAttack();
                } else {
                    logMessage("Error: AI_MAKING_MOVE phase but non-AI player.");
                    switchTurns();
                }
                break;
            case GAME_OVER:
                setConfirmButtonAction("Reset Game", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        resetGame();
                    }
                });
                attackerWeaponPanel.setVisible(false);
                revealShipsButton.setVisible(false);
                break;
        }
    }

    private void promptGameMode() {
        String[] options = {"Two Players", "Play with AI"};
        int choice = JOptionPane.showOptionDialog(this, "Select Game Mode:", "Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 1) {
            isVsAI = true;
            logMessage("Game Mode: Play with AI selected.");
        } else {
            isVsAI = false;
            logMessage("Game Mode: Two Players selected.");
        }
        advancePhase();
    }

    private void promptForPlayerName(int playerNumber) {
        String name = JOptionPane.showInputDialog(this, "Enter name for Player " + playerNumber + ":", "Player Setup", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            name = "Player " + playerNumber;
        }
        int initialBudget = Player.INITIAL_BUDGET;

        if (playerNumber == 1) {
            player1 = new Player(name.trim(), initialBudget);
            logMessage("Player 1 set as: " + player1.getName());
        } else {
            if (!isVsAI) {
                player2 = new Player(name.trim(), initialBudget);
                logMessage("Player 2 set as: " + player2.getName());
            }
        }
        advancePhase();
    }

    private void prepareShipPlacement() {
        tempSelectedShipCells.clear();
        statusLabel.setText(currentAttacker.getName() + ": Select " + Board.SHIPS_COUNT + " cells for your ships. Click on your grid.");
        setConfirmButtonAction("Done Placing Ships", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizeShipPlacement();
            }
        });
        confirmButton.setEnabled(false);
        attackerWeaponPanel.setVisible(false);
        revealShipsButton.setVisible(false);
        configureGridInteractivityForPlacement();
        refreshGuiGrids();
    }

    private void handleShipCellSelection(int row, int col) {
        Point clickedPoint = new Point(row, col);
        Board currentBoard = currentAttacker.getBoard();

        if (tempSelectedShipCells.contains(clickedPoint)) {
            tempSelectedShipCells.remove(clickedPoint);
        } else {
            if (tempSelectedShipCells.size() >= Board.SHIPS_COUNT) {
                JOptionPane.showMessageDialog(this, "Max " + Board.SHIPS_COUNT + " ships.", "Selection Limit", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (currentBoard.canBePlacedAt(row, col)) {
                boolean adjacentToTempSelected = false;
                for (Point tempCell : tempSelectedShipCells) {
                    if (Math.abs(tempCell.x - row) <= 1 && Math.abs(tempCell.y - col) <= 1) {
                        adjacentToTempSelected = true;
                        break;
                    }
                }
                if (adjacentToTempSelected) {
                    JOptionPane.showMessageDialog(this, "Cannot select adjacent cells.", "Placement Rule", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                tempSelectedShipCells.add(clickedPoint);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid cell (occupied or near existing ship).", "Placement Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        confirmButton.setEnabled(tempSelectedShipCells.size() == Board.SHIPS_COUNT);
        statusLabel.setText(currentAttacker.getName() + ": " + tempSelectedShipCells.size() + "/" + Board.SHIPS_COUNT + " ships selected.");
        refreshGuiGrids();
    }

    private void finalizeShipPlacement() {
        if (tempSelectedShipCells.size() != Board.SHIPS_COUNT) {
            JOptionPane.showMessageDialog(this, "Select " + Board.SHIPS_COUNT + " cells.", "Placement Incomplete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            for (Point cellPos : tempSelectedShipCells) {
                currentAttacker.placeShip(cellPos.x, cellPos.y);
            }
            logMessage(currentAttacker.getName() + " placed all ships.");
            FileLogger.history(currentAttacker.getName() + "'s ships placed:");
            logBoardToFile(currentAttacker.getBoard(), true, currentAttacker.getName() + "'s final ship positions:");

            tempSelectedShipCells.clear();
            disableAllGridInteractivity();
            refreshGuiGrids();

            if (currentAttacker == player1) {
                currentAttacker = player2;
                currentDefender = player1;
                currentPhase = GamePhase.PLAYER2_SHIP_PLACEMENT;
                if (isVsAI && player2 instanceof PlayerAI) {
                    handleAIShipPlacement();
                } else {
                    prepareShipPlacement();
                }
            } else {
                currentPhase = GamePhase.PRE_PURCHASE_SHIP_HIDING;
                advancePhase();
            }
        } catch (InvalidPlacementException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage() + "\nPlease re-select.", "Placement Error", JOptionPane.ERROR_MESSAGE);
            FileLogger.error(currentAttacker.getName() + " final placement error: " + e.getMessage());
            tempSelectedShipCells.clear();
            refreshGuiGrids();
            prepareShipPlacement();
        }
    }

    private void handleAIShipPlacement() {
        if (!(player2 instanceof PlayerAI)) {
            return;
        }
        PlayerAI ai = (PlayerAI) player2;
        statusLabel.setText(ai.getName() + " is placing ships...");
        logMessage(ai.getName() + " is placing ships automatically...");
        confirmButton.setVisible(false);
        disableAllGridInteractivity();

        ai.placeShipsAutomatically();

        logMessage(ai.getName() + " finished placing ships.");
        FileLogger.history(ai.getName() + "'s ships placed:");
        logBoardToFile(ai.getBoard(), true, ai.getName() + "'s final ship positions:");
        refreshGuiGrids();
        currentPhase = GamePhase.PRE_PURCHASE_SHIP_HIDING;
        advancePhase();
    }

    private void handleAIAttack() {
        if (!(currentAttacker instanceof PlayerAI)) return;
        PlayerAI ai = (PlayerAI) currentAttacker;

        statusLabel.setText(ai.getName() + " is attacking...");
        logMessage(ai.getName() + " is attacking automatically...");
        confirmButton.setVisible(false);
        attackerWeaponPanel.setVisible(false);
        revealShipsButton.setVisible(false);
        disableAllGridInteractivity();

        ai.performAttackAutomatically(currentDefender);

        logMessage(ai.getName() + " completed its attack.");
        refreshGuiGrids();

        if (currentDefender.checkDefeat()) {
            FileLogger.history(ai.getName() + " won the game!");
            endGame(ai.getName());
        } else {
            switchTurns();
        }
    }

    private void disableAllGridInteractivity() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                for (ActionListener al : player1Buttons[r][c].getActionListeners())
                    player1Buttons[r][c].removeActionListener(al);
                player1Buttons[r][c].setEnabled(false);
                for (ActionListener al : player2Buttons[r][c].getActionListeners())
                    player2Buttons[r][c].removeActionListener(al);
                player2Buttons[r][c].setEnabled(false);
            }
        }
    }


    private void prepareWeaponPurchase() {
        statusLabel.setText(currentAttacker.getName() + ": Purchase your weapons.");
        confirmButton.setVisible(false);
        attackerWeaponPanel.setVisible(false);
        revealShipsButton.setVisible(false);
        showWeaponPurchaseDialog();
    }

    private void showWeaponPurchaseDialog() {
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ArrayList<WeaponType> purchasableWeaponTypes = new ArrayList<>();
        ArrayList<JSpinner> spinners = new ArrayList<>();
        ArrayList<JLabel> subtotalLabelsList = new ArrayList<>();

        JLabel totalCostLabel = new JLabel("Total Cost: $0");
        JLabel budgetLabel = new JLabel("Your Budget: $" + currentAttacker.getBudget());

        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogPanel.add(budgetLabel, gbc);
        gbc.gridy++;
        dialogPanel.add(totalCostLabel, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;
        for (WeaponType wt : WeaponType.values()) {
            if (wt == WeaponType.BASIC) continue;
            purchasableWeaponTypes.add(wt);
            gbc.gridx = 0;
            dialogPanel.add(new JLabel(wt.getDisplayName() + " ($" + wt.getPrice() + "):"), gbc);
            gbc.gridx = 1;
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
            spinners.add(spinner);
            dialogPanel.add(spinner, gbc);
            gbc.gridx = 2;
            JLabel itemSubtotal = new JLabel("$0");
            subtotalLabelsList.add(itemSubtotal);
            dialogPanel.add(itemSubtotal, gbc);
            gbc.gridy++;
        }

        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int currentTotalCost = 0;
                for (int i = 0; i < purchasableWeaponTypes.size(); i++) {
                    WeaponType wt = purchasableWeaponTypes.get(i);
                    JSpinner s = spinners.get(i);
                    JLabel subLabel = subtotalLabelsList.get(i);
                    int quantity = (Integer) s.getValue();
                    int itemCost = quantity * wt.getPrice();
                    subLabel.setText("$" + itemCost);
                    currentTotalCost += itemCost;
                }
                totalCostLabel.setText("Total Cost: $" + currentTotalCost);
                totalCostLabel.setForeground(currentTotalCost > currentAttacker.getBudget() ? Color.RED : Color.BLACK);
            }
        };

        for (JSpinner s : spinners) s.addChangeListener(listener);
        listener.stateChanged(null);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel,
                currentAttacker.getName() + " - Buy Weapons", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int finalCost = 0;
            for (int i = 0; i < purchasableWeaponTypes.size(); i++) {
                finalCost += (Integer) spinners.get(i).getValue() * purchasableWeaponTypes.get(i).getPrice();
            }
            if (finalCost > currentAttacker.getBudget()) {
                JOptionPane.showMessageDialog(this, "Cannot afford. Cost exceeds budget.", "Budget Error", JOptionPane.ERROR_MESSAGE);
                showWeaponPurchaseDialog();
                return;
            }
            for (int i = 0; i < purchasableWeaponTypes.size(); i++) {
                WeaponType wt = purchasableWeaponTypes.get(i);
                int quantity = (Integer) spinners.get(i).getValue();
                for (int j = 0; j < quantity; j++) {
                    try {
                        currentAttacker.buyWeapon(wt.getName());
                    } catch (IllegalWeaponTypeException ex) {
                        logMessage("Error buying " + wt.getDisplayName() + ": " + ex.getMessage());
                        FileLogger.error(currentAttacker.getName() + " purchase error: " + ex.getMessage());
                        break;
                    }
                }
            }
            logMessage(currentAttacker.getName() + " updated budget: $" + currentAttacker.getBudget());
            FileLogger.history(currentAttacker.getName() + " inventory: " + currentAttacker.getInventory().size() + " weapons, Budget: $" + currentAttacker.getBudget());
        } else {
            logMessage(currentAttacker.getName() + " skipped weapon purchasing.");
        }
        updatePlayerWeaponButtons(currentAttacker);

        if (currentAttacker == player1) {
            currentAttacker = player2;
            currentDefender = player1;
            currentPhase = GamePhase.PLAYER2_WEAPON_PURCHASE;
            if (isVsAI && player2 instanceof PlayerAI) {
                handleAIPurchase();
            } else {
                prepareWeaponPurchase();
            }
        } else {
            currentPhase = GamePhase.START_ATTACK_PROMPT;
            advancePhase();
        }
    }

    private void handleAIPurchase() {
        if (!(player2 instanceof PlayerAI)) return;
        PlayerAI ai = (PlayerAI) player2;
        statusLabel.setText(ai.getName() + " is purchasing weapons...");
        logMessage(ai.getName() + " is purchasing weapons automatically...");
        confirmButton.setVisible(false);
        disableAllGridInteractivity();

        ai.purchaseWeaponsAutomatically();

        logMessage(ai.getName() + " finished purchasing. Budget: $" + ai.getBudget());
        FileLogger.history(ai.getName() + " inventory: " + ai.getInventory().size() + " weapons, Budget: $" + ai.getBudget());
        currentPhase = GamePhase.START_ATTACK_PROMPT;
        advancePhase();
    }

    private void updatePlayerWeaponButtons(Player player) {
        attackerWeaponPanel.removeAll();
        weaponButtonGroup = new ButtonGroup();

        JRadioButton basicRadio = new JRadioButton(WeaponType.BASIC.getDisplayName() + " (Free)");
        basicRadio.setActionCommand(WeaponType.BASIC.getName());
        basicRadio.setSelected(true);
        weaponButtonGroup.add(basicRadio);
        attackerWeaponPanel.add(basicRadio);

        if (player != null && player.getInventory() != null) {
            for (WeaponType typeToDisplay : WeaponType.values()) {
                if (typeToDisplay == WeaponType.BASIC) continue;
                long count = 0;
                for (Weapon w : player.getInventory()) {
                    if (w.getName().equals(typeToDisplay.getName())) {
                        count++;
                    }
                }
                if (count > 0) {
                    JRadioButton weaponRadio = new JRadioButton(typeToDisplay.getDisplayName() + " (x" + count + ")");
                    weaponRadio.setActionCommand(typeToDisplay.getName());
                    weaponButtonGroup.add(weaponRadio);
                    attackerWeaponPanel.add(weaponRadio);
                }
            }
        }
        attackerWeaponPanel.revalidate();
        attackerWeaponPanel.repaint();
    }

    private void prepareAttackTurn() {
        statusLabel.setText(currentAttacker.getName() + "'s Turn. Attack " + currentDefender.getName() + "!");
        confirmButton.setVisible(false);
        attackerWeaponPanel.setVisible(true);
        revealShipsButton.setVisible(true);
        revealShipsButton.setSelected(showCurrentPlayerShipsHint);
        updatePlayerWeaponButtons(currentAttacker);
        configureGridInteractivityForAttack();
        refreshGuiGrids();
    }

    private void handleAttackCellClick(int r, int c) {
        if (weaponButtonGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Select a weapon.", "No Weapon Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedWeaponName = weaponButtonGroup.getSelection().getActionCommand();

        try {
            currentAttacker.useWeapon(selectedWeaponName, currentDefender, r, c);
            logMessage(currentAttacker.getName() + " used " + selectedWeaponName + " at (" + r + "," + c + ") on " + currentDefender.getName() +
                    ". Weapons left: " + currentAttacker.getWeaponCount());
            FileLogger.history(currentAttacker.getName() + " used " + selectedWeaponName + " at (" + r + ", " + c + ") on " + currentDefender.getName() +
                    " (Weapons left: " + currentAttacker.getWeaponCount() + ")");
            updatePlayerWeaponButtons(currentAttacker);

            if (currentDefender.checkDefeat()) {
                FileLogger.history(currentAttacker.getName() + " won the game!");
                endGame(currentAttacker.getName());
            } else {
                switchTurns();
            }
        } catch (AlreadyAttackedException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Attack Error", JOptionPane.ERROR_MESSAGE);
            FileLogger.error(currentAttacker.getName() + " attack error: " + e.getMessage());
        } catch (IllegalWeaponTypeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Weapon Error", JOptionPane.WARNING_MESSAGE);
            FileLogger.error(currentAttacker.getName() + " weapon error: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Game Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            FileLogger.error(currentAttacker.getName() + " general error: " + e.getMessage());
        }
        refreshGuiGrids();
    }

    private void switchTurns() {
        showCurrentPlayerShipsHint = false;
        revealShipsButton.setSelected(false);

        Player temp = currentAttacker;
        currentAttacker = currentDefender;
        currentDefender = temp;

        if (currentAttacker == player1) {
            currentPhase = GamePhase.PLAYER1_TURN;
        } else {
            currentPhase = GamePhase.PLAYER2_TURN;
        }

        logMessage("Turn changed. It's now " + currentAttacker.getName() + "'s turn.");
        advancePhase();
    }

    private void configureGridInteractivityForPlacement() {
        JButton[][] placementButtons = (currentAttacker == player1) ? player1Buttons : player2Buttons;
        JButton[][] otherButtons = (currentAttacker == player1) ? player2Buttons : player1Buttons;
        disableAllGridInteractivity();

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                otherButtons[r][c].setEnabled(false);
                if (!currentAttacker.getBoard().getCellAt(r, c).isOccupied()) {
                    placementButtons[r][c].setEnabled(true);
                    final int finalR = r;
                    final int finalC = c;
                    placementButtons[r][c].addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            handleShipCellSelection(finalR, finalC);
                        }
                    });
                } else {
                    placementButtons[r][c].setEnabled(false);
                }
            }
        }
    }

    private void configureGridInteractivityForAttack() {
        JButton[][] defenderButtons = (currentAttacker == player1) ? player2Buttons : player1Buttons;
        JButton[][] attackerOwnButtons = (currentAttacker == player1) ? player1Buttons : player2Buttons;
        Board defenderBoard = currentDefender.getBoard();
        disableAllGridInteractivity();

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                attackerOwnButtons[r][c].setEnabled(false);
                if (!defenderBoard.getCellAt(r, c).isHit()) {
                    defenderButtons[r][c].setEnabled(true);
                    final int finalR = r;
                    final int finalC = c;
                    defenderButtons[r][c].addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            handleAttackCellClick(finalR, finalC);
                        }
                    });
                } else {
                    defenderButtons[r][c].setEnabled(false);
                }
            }
        }
    }

    private void refreshGuiGrids() {
        if (player1 == null || player2 == null) return;

        boolean p1Placement = (currentAttacker == player1 && currentPhase == GamePhase.PLAYER1_SHIP_PLACEMENT);
        boolean p2Placement = (currentAttacker == player2 && currentPhase == GamePhase.PLAYER2_SHIP_PLACEMENT);

        refreshSingleGridDisplay(player1Buttons, player1.getBoard(), player1, p1Placement);
        refreshSingleGridDisplay(player2Buttons, player2.getBoard(), player2, p2Placement);
        updatePanelTitles();
    }

    private void setButtonAppearance(JButton button, ImageIcon icon) {
        button.setText("");
        button.setIcon(icon);
        if (icon != null) {
            button.setDisabledIcon(icon);
        }
    }


    private void refreshSingleGridDisplay(JButton[][] gridButtons, Board board, Player boardOwner, boolean isPlacementPhaseForThisPlayer) {
        if (board == null) return;
        ImageIcon currentShipIcon = (boardOwner == player1) ? ship1Icon : ship2Icon;
        ImageIcon currentSunkShipIcon = (boardOwner == player1) ? ship1SunkIcon : ship2SunkIcon;


        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Cell cell = board.getCellAt(r, c);
                JButton button = gridButtons[r][c];

                boolean revealThisPlayerShips;
                if (currentPhase == GamePhase.GAME_OVER) {
                    revealThisPlayerShips = true;
                } else if (currentPhase == GamePhase.PRE_PURCHASE_SHIP_HIDING) {
                    revealThisPlayerShips = false;
                } else if (boardOwner == currentAttacker) {
                    revealThisPlayerShips = showCurrentPlayerShipsHint || isPlacementPhaseForThisPlayer;
                } else {
                    revealThisPlayerShips = false;
                }

                if (isPlacementPhaseForThisPlayer && tempSelectedShipCells.contains(new Point(r, c))) {
                    setButtonAppearance(button, currentShipIcon);
                } else {
                    if (cell.isOccupied()) {
                        if (cell.isHit()) {
                            setButtonAppearance(button, currentSunkShipIcon);
                        } else {
                            if (revealThisPlayerShips) {
                                setButtonAppearance(button, currentShipIcon);
                            } else {
                                setButtonAppearance(button, null);
                            }
                        }
                    } else {
                        if (cell.isHit()) {
                            setButtonAppearance(button, splashIcon);
                        } else {
                            setButtonAppearance(button, null);
                        }
                    }
                }
            }
        }
    }


    private void updatePanelTitles() {
        String p1Name = (player1 != null) ? player1.getName() : "Player 1";
        String p2Name = (player2 != null) ? player2.getName() : "Player 2";
        String p1Title = p1Name + "'s Waters";
        String p2Title = p2Name + "'s Waters";

        if (currentPhase == GamePhase.PLAYER1_SHIP_PLACEMENT) p1Title += " (Placing Ships)";
        else if (currentPhase == GamePhase.PLAYER2_SHIP_PLACEMENT) p2Title += " (Placing Ships)";
        else if (currentPhase == GamePhase.PLAYER1_TURN) {
            p1Title += " (Your Ships)";
            p2Title += " (Attack Here!)";
        } else if (currentPhase == GamePhase.PLAYER2_TURN) {
            p1Title += " (Attack Here!)";
            p2Title += " (Your Ships)";
        } else if (currentPhase == GamePhase.GAME_OVER) {
            p1Title += " (Final Board)";
            p2Title += " (Final Board)";
        }

        if (player1GridPanel != null) ((TitledBorder) player1GridPanel.getBorder()).setTitle(p1Title);
        if (player2GridPanel != null) ((TitledBorder) player2GridPanel.getBorder()).setTitle(p2Title);
        if (player1GridPanel != null) player1GridPanel.repaint();
        if (player2GridPanel != null) player2GridPanel.repaint();
    }

    private void logBoardToFile(Board board, boolean revealShips, String header) {
        FileLogger.history("\n" + header);
        for (int i = 0; i < GRID_SIZE; i++) {
            String line = "[ ";
            for (int j = 0; j < GRID_SIZE; j++) {
                line += board.getCellAt(i, j).toString(revealShips) + " ";
            }
            line += "]";
            FileLogger.history(line);
        }
        FileLogger.history("");
    }

    public void logMessage(String message) {
        messageArea.append(message + "\n");
    }

    private void resetGame() {
        logMessage("Resetting game...");
        player1 = null;
        player2 = null;
        isVsAI = false;
        currentAttacker = null;
        currentDefender = null;
        tempSelectedShipCells.clear();
        showCurrentPlayerShipsHint = false;
        clearGridDisplay(player1Buttons);
        clearGridDisplay(player2Buttons);
        if (player1GridPanel != null) ((TitledBorder) player1GridPanel.getBorder()).setTitle("Player 1's Waters");
        if (player2GridPanel != null) ((TitledBorder) player2GridPanel.getBorder()).setTitle("Player 2's Waters");
        if (player1GridPanel != null) player1GridPanel.repaint();
        if (player2GridPanel != null) player2GridPanel.repaint();

        messageArea.setText("");
        statusLabel.setText("Welcome to Battle of the Sea!");

        setConfirmButtonAction("Start Game", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentPhase = GamePhase.INITIALIZING;
                advancePhase();
            }
        });

        attackerWeaponPanel.removeAll();
        attackerWeaponPanel.setVisible(false);
        weaponButtonGroup = new ButtonGroup();
        revealShipsButton.setVisible(false);
        revealShipsButton.setSelected(false);

        logMessage("Game has been reset. Click Start Game.");
    }

    private void clearGridDisplay(JButton[][] gridButtons) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (gridButtons[r][c] != null) {
                    gridButtons[r][c].setBackground(null);
                    gridButtons[r][c].setText("");
                    gridButtons[r][c].setIcon(null);
                    gridButtons[r][c].setDisabledIcon(null);
                    gridButtons[r][c].setEnabled(false);
                    for (ActionListener al : gridButtons[r][c].getActionListeners())
                        gridButtons[r][c].removeActionListener(al);
                }
            }
        }
    }

    private void endGame(String winnerName) {
        currentPhase = GamePhase.GAME_OVER;
        statusLabel.setText("Game Over! " + winnerName + " wins!");
        logMessage("Game Over! " + winnerName + " wins! Congratulations!");
        FileLogger.history("Game Over! " + winnerName + " wins! Congratulations!");
        setConfirmButtonAction("Reset Game", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        confirmButton.setVisible(false);
        attackerWeaponPanel.setVisible(false);
        revealShipsButton.setVisible(false);
        showCurrentPlayerShipsHint = false;

        disableAllGridInteractivity();
        refreshGuiGrids();
    }

    class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(ImageIcon icon) {
            this.backgroundImage = icon.getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
