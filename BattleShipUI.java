import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleShipUI {
    private BattleShips game;  // The game logic object
    private JButton[][] playerGridButtons;  // Buttons for the player's grid
    private JButton[][] computerGridButtons;  // Buttons for the computer's grid
    private JFrame window;  // Main window for the game
    private JPanel playerPanel, computerPanel;  // Panels to hold the player and computer grids
    private boolean isPlayerTurn = true;  // A flag to track whose turn it is

    // Constructor: Creates a new game and sets up the user interface
    public BattleShipUI() {
        game = new BattleShips();  // Create a new instance of the game logic
        setupUI();  // Setup the graphical user interface
        game.deployComputerShips();  // Deploy the computer's ships on the grid
    }

    // Sets up the graphical user interface (UI) for the game
    private void setupUI() {
        window = new JFrame("Battleship Game");  // Create the main window with a title
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the app when the window is closed
        window.setSize(600, 600);  // Set the size of the window
        window.setLayout(new GridLayout(1, 2));  // Split the window into two halves (player and computer grids)

        playerPanel = new JPanel(new GridLayout(10, 10));  // Create a 10x10 grid for the player's panel
        computerPanel = new JPanel(new GridLayout(10, 10));  // Create a 10x10 grid for the computer's panel

        playerGridButtons = new JButton[10][10];  // Create an array of buttons for the player's grid
        computerGridButtons = new JButton[10][10];  // Create an array of buttons for the computer's grid

        // Initialize the player's grid (buttons to attack the computer's ships)
        setupGrid(playerGridButtons, playerPanel, false);
        // Initialize the computer's grid (buttons to receive player's attacks)
        setupGrid(computerGridButtons, computerPanel, true);

        window.add(playerPanel);  // Add the player's grid to the window
        window.add(computerPanel);  // Add the computer's grid to the window
        window.setVisible(true);  // Make the window visible
    }

    // Creates a grid of buttons and adds them to the given panel
    private void setupGrid(JButton[][] gridButtons, JPanel panel, boolean isComputerGrid) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                gridButtons[row][col] = new JButton();  // Create a new button for each grid cell
                gridButtons[row][col].setBackground(Color.BLUE);  // Set the default color to blue (water)
                final int currentRow = row, currentCol = col;  // Final variables for the button's row and column
                if (isComputerGrid) {
                    // If it's the computer's grid, add an action listener to handle attacks from the player
                    gridButtons[row][col].addActionListener(e -> handlePlayerAttack(currentRow, currentCol));
                }
                panel.add(gridButtons[row][col]);  // Add the button to the panel
            }
        }
    }

    // Handles the player's attack on a specific cell of the computer's grid
    private void handlePlayerAttack(int row, int col) {
        if (!isPlayerTurn) return;  // If it's not the player's turn, don't do anything

        // Perform the player's attack and get the result (hit or miss)
        String result = game.playerAttack(row, col);
        // Change the color of the button based on the attack result
        computerGridButtons[row][col].setBackground(result.equals("HIT") ? Color.RED : Color.GRAY);
        isPlayerTurn = false;  // It's now the computer's turn

        // If the game is not over, let the computer make a move after a 1-second delay
        if (!game.isGameOver()) {
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleComputerTurn();  // Let the computer take its turn
                }
            });
            timer.setRepeats(false);  // Don't repeat the timer
            timer.start();  // Start the timer
        } else {
            showGameOverMessage();  // Show the game over message if the game is finished
        }
    }

    // Handles the computer's turn (attacking the player's grid)
    private void handleComputerTurn() {
        // Perform the computer's attack and get the result (hit or miss)
        String result = game.computerAttack();
        updatePlayerGrid();  // Update the player's grid based on the attack result
        isPlayerTurn = true;  // It's now the player's turn again

        if (game.isGameOver()) {
            showGameOverMessage();  // Show the game over message if the game is finished
        }
    }

    // Updates the player's grid to reflect the results of the computer's attacks
    private void updatePlayerGrid() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (game.getPlayerGrid()[row][col].equals("X")) {
                    playerGridButtons[row][col].setBackground(Color.RED);  // Mark hits with red
                } else if (game.getPlayerGrid()[row][col].equals("-")) {
                    playerGridButtons[row][col].setBackground(Color.GRAY);  // Mark misses with gray
                }
            }
        }
    }

    // Displays a message indicating who won the game and then closes the game window
    private void showGameOverMessage() {
        String message = game.getNumPlayerShips() == 0 ? "Computer Wins!" : "Player Wins!";
        JOptionPane.showMessageDialog(window, message);  // Show a dialog with the game result
        window.dispose();  // Close the window
    }

    // Main method to start the Battleship game
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BattleShipUI::new);  // Run the game in the Swing event thread
    }
}
