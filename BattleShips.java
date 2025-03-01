import java.util.*;

public class BattleShips {
    private final int numRows = 10;  // Number of rows on the grid
    private final int numCols = 10;  // Number of columns on the grid
    private int numPlayerShips;  // The number of ships the player has left
    private int numComputerShips;  // The number of ships the computer has left
    private String[][] playerGridButtons;  // Grid representing the player's ships
    private String[][] computerGridButtons;  // Grid representing the computer's ships
    private Random random = new Random();  // Random object for generating random placements

    // Constructor initializes the game boards (player and computer)
    public BattleShips() {
        playerGridButtons = new String[numRows][numCols];  // Initialize player's grid
        computerGridButtons = new String[numRows][numCols];  // Initialize computer's grid
        initializeGrids();  // Initialize both grids to be empty
    }

    // Initializes both grids with empty spaces
    private void initializeGrids() {
        for (int row = 0; row < numRows; row++) {
            Arrays.fill(playerGridButtons[row], " ");  // Empty spaces for the player's grid
            Arrays.fill(computerGridButtons[row], " ");  // Empty spaces for the computer's grid
        }
    }

    // Places a player's ship on the grid if the placement is valid
    public boolean placePlayerShip(int x, int y, int shipLength, boolean isHorizontal) {
        if (!isPlacementValid(playerGridButtons, x, y, shipLength, isHorizontal)) {
            return false;  // Return false if the placement is not valid
        }
        placeShipOnGrid(playerGridButtons, x, y, shipLength, isHorizontal);  // Place the ship on the player's grid
        numPlayerShips++;  // Increase the number of ships the player has
        return true;  // Return true if the placement is successful
    }

    // Randomly deploys the computer's ships on the grid
    public void deployComputerShips() {
        int[] shipSizes = {5, 4, 3, 2};  // Array of ship sizes for the computer
        numComputerShips = shipSizes.length;  // Set the number of ships the computer has

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int x = random.nextInt(numRows);  // Random x-coordinate
                int y = random.nextInt(numCols);  // Random y-coordinate
                boolean isHorizontal = random.nextBoolean();  // Random orientation (horizontal or vertical)
                if (isPlacementValid(computerGridButtons, x, y, size, isHorizontal)) {
                    placeShipOnGrid(computerGridButtons, x, y, size, isHorizontal);  // Place the computer's ship
                    placed = true;  // Mark the ship as placed
                }
            }
        }
    }

    // Checks if a ship can be placed at the given coordinates
    private boolean isPlacementValid(String[][] grid, int x, int y, int shipLength, boolean isHorizontal) {
        if (isHorizontal) {
            if (y + shipLength > numCols) return false;  // Out of bounds check (horizontal)
            for (int i = 0; i < shipLength; i++) {
                if (!grid[x][y + i].equals(" ")) return false;  // Check for collisions (horizontal)
            }
        } else {
            if (x + shipLength > numRows) return false;  // Out of bounds check (vertical)
            for (int i = 0; i < shipLength; i++) {
                if (!grid[x + i][y].equals(" ")) return false;  // Check for collisions (vertical)
            }
        }
        return true;  // Valid placement
    }

    // Places a ship on the specified grid (either player or computer)
    private void placeShipOnGrid(String[][] grid, int x, int y, int shipLength, boolean isHorizontal) {
        for (int i = 0; i < shipLength; i++) {
            if (isHorizontal) {
                grid[x][y + i] = "S";  // Place a ship horizontally
            } else {
                grid[x + i][y] = "S";  // Place a ship vertically
            }
        }
    }

    // Handles a player's attack on the computer's grid
    public String playerAttack(int x, int y) {
        if (computerGridButtons[x][y].equals("S")) {  // Hit condition
            computerGridButtons[x][y] = "X";  // Mark as hit
            numComputerShips--;  // Decrease the computer's ship count
            return "HIT";  // Return "HIT" if the attack was successful
        } else {  // Miss condition
            computerGridButtons[x][y] = "-";  // Mark as miss
            return "MISS";  // Return "MISS" if the attack was a miss
        }
    }

    // Handles a computer's attack on the player's grid
    public String computerAttack() {
        int x, y;
        do {
            x = random.nextInt(numRows);  // Random x-coordinate
            y = random.nextInt(numCols);  // Random y-coordinate
        } while (!playerGridButtons[x][y].equals(" ") && !playerGridButtons[x][y].equals("S"));  // Ensure valid attack location

        if (playerGridButtons[x][y].equals("S")) {  // Hit condition
            playerGridButtons[x][y] = "X";  // Mark as hit
            numPlayerShips--;  // Decrease the player's ship count
            return "Computer hit at (" + x + "," + y + ")";  // Return the result of the attack
        } else {  // Miss condition
            playerGridButtons[x][y] = "-";  // Mark as miss
            return "Computer missed at (" + x + "," + y + ")";  // Return the result of the miss
        }
    }

    // Checks if the game is over (either player or computer has no ships left)
    public boolean isGameOver() {
        return numPlayerShips == 0 || numComputerShips == 0;  // Game over if any player's ships are 0
    }

    // Getter method for the player's grid
    public String[][] getPlayerGrid() {
        return playerGridButtons;  // Return the player's grid
    }

    // Getter method for the number of ships the player has left
    public int getNumPlayerShips() {
        return numPlayerShips;  // Return the number of player's ships left
    }
}
