import java.util.*;

// Harold:
// What's up guys. The code has been completely revamped due to the game not being complete.
// The main problem the code faced was playing the whole game on one board. Now the boards are split using two
// different grids.

// Further changes below:
// - Added different ship sizes. Specifically 5, 4, 3, 2.
// - While adding your ships to the table, you now see live updates of your map.

public class BattleShips {
    public static int numRows = 10;
    public static int numCols = 10;
    public static int playerShips;
    public static int computerShips;

    // Added feature where each player has their own board. The original code only used one board.
    public static String[][] playerGrid = new String[numRows][numCols];
    public static String[][] computerGrid = new String[numRows][numCols];

    // This will be the list of options the AI considers stored in an ArrayList.
    public static ArrayList<int[]> targetList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("**** WELCOME TO BATTLESHIP VI! ****");
        System.out.println("Loading...\n");
        initBoards();

        deployPlayerShips();
        deployComputerShips();

        // The game loops until one side loses all of their ships.
        do {
            playerTurn();
            computerTurn();
        } while (playerShips > 0 && computerShips > 0);

        gameOver();
    }

    // Initialize both boards with blank spaces.
    public static void initBoards() {
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                playerGrid[i][j] = " ";
                computerGrid[i][j] = " ";
            }
        }
    }

    // Print player's board.
    public static void printPlayerBoard() {
        System.out.println("\nYour Board:");
        System.out.print("  ");
        for (int i = 0; i < numCols; i++)
            System.out.print(i);
        System.out.println();
        for (int i = 0; i < numRows; i++){
            System.out.print(i + "|");
            for (int j = 0; j < numCols; j++){
                System.out.print(playerGrid[i][j]);
            }
            System.out.println("|" + i);
        }
        System.out.print("  ");
        for (int i = 0; i < numCols; i++)
            System.out.print(i);
        System.out.println();
    }

    // Print computer's board for the player (shows only hits and misses).
    public static void printComputerBoard() {
        System.out.println("\nComputer Board:");
        System.out.print("  ");
        for (int i = 0; i < numCols; i++)
            System.out.print(i);
        System.out.println();
        for (int i = 0; i < numRows; i++){
            System.out.print(i + "|");
            for (int j = 0; j < numCols; j++){
                // Hide unhit ship positions: if the cell has "5", "4", "3", or "2" (ship marker), print a blank.
                if (computerGrid[i][j].equals("5") || computerGrid[i][j].equals("4") ||
                        computerGrid[i][j].equals("3") || computerGrid[i][j].equals("2")) {
                    System.out.print(" ");
                } else {
                    System.out.print(computerGrid[i][j]);
                }
            }
            System.out.println("|" + i);
        }
        System.out.print("  ");
        for (int i = 0; i < numCols; i++)
            System.out.print(i);
        System.out.println();
    }

    // Deploy player's ships on playerGrid.
    // We use four ships with lengths 5, 4, 3, and 2.
    public static void deployPlayerShips() {
        Scanner input = new Scanner(System.in);
        int[] shipSizes = {5, 4, 3, 2};
        playerShips = shipSizes.length;
        System.out.println("\nDeploy your ships!");

        for (int i = 0; i < shipSizes.length; i++){

            int shipLength = shipSizes[i];
            boolean valid = false;

            while (!valid) {
                System.out.print("This ship is " + shipLength + " tiles long. Enter its X coordinate: ");
                int x = input.nextInt();
                System.out.print("This ship is " + shipLength + " tiles long. Enter its Y coordinate: ");
                int y = input.nextInt();

                //So whichever coordinate you enter, that's where the ship begins.
                //e.g. entering (3,3,V) for a 3-tile ship covers (3,3), (4,3), (5,3).
                System.out.print("Enter orientation (H for horizontal, V for vertical): ");
                char orientation = input.next().charAt(0);
                valid = true;

                // Validate placement on player's board.
                if (orientation == 'H' || orientation == 'h'){
                    if (y + shipLength > numCols) valid = false;
                    else {
                        for (int j = 0; j < shipLength; j++){
                            if (!playerGrid[x][y+j].equals(" ")){
                                valid = false;
                                break;
                            }
                        }
                    }
                } else if (orientation == 'V' || orientation == 'v'){
                    if (x + shipLength > numRows) valid = false;
                    else {
                        for (int j = 0; j < shipLength; j++){
                            if (!playerGrid[x+j][y].equals(" ")){
                                valid = false;
                                break;
                            }
                        }
                    }
                } else {
                    valid = false;
                }
                if (!valid)
                    System.out.println("Invalid placement. Try again.");
                else {
                    // Place the ship (each cell is marked with the ship's length as a string).
                    if (orientation == 'H' || orientation == 'h'){
                        for (int j = 0; j < shipLength; j++){
                            playerGrid[x][y+j] = Integer.toString(shipLength);
                        }
                    } else {
                        for (int j = 0; j < shipLength; j++){
                            playerGrid[x+j][y] = Integer.toString(shipLength);
                        }
                    }
                }
            }
            printPlayerBoard();
        }
    }

    // Deploy computer's ships on computerGrid.
    public static void deployComputerShips() {
        System.out.println("\nComputer is deploying ships");
        int[] shipSizes = {5, 4, 3, 2};
        computerShips = shipSizes.length;
        Random random = new Random();
        for (int i = 0; i < shipSizes.length; i++){
            int shipLength = shipSizes[i];
            boolean valid = false;
            while (!valid) {
                int x = random.nextInt(numRows);
                int y = random.nextInt(numCols);
                char orientation = random.nextBoolean() ? 'H' : 'V';
                valid = true;
                if (orientation == 'H'){
                    if (y + shipLength > numCols) valid = false;
                    else {
                        for (int j = 0; j < shipLength; j++){
                            if (!computerGrid[x][y+j].equals(" ")){
                                valid = false;
                                break;
                            }
                        }
                    }
                } else {
                    if (x + shipLength > numRows) valid = false;
                    else {
                        for (int j = 0; j < shipLength; j++){
                            if (!computerGrid[x+j][y].equals(" ")){
                                valid = false;
                                break;
                            }
                        }
                    }
                }
                if (valid) {
                    if (orientation == 'H'){
                        for (int j = 0; j < shipLength; j++){
                            computerGrid[x][y+j] = Integer.toString(shipLength);
                        }
                    } else {
                        for (int j = 0; j < shipLength; j++){
                            computerGrid[x+j][y] = Integer.toString(shipLength);
                        }
                    }
                    System.out.println("Computer deployed a ship of length " + shipLength);
                }
            }
            //THIS AREA IS RESERVED FOR IF WE WANT TO LOOK AT THE COMPUTER'S BOARD FOR DEBUGGING PURPOSES.
        }
    }

    // Player's turn: the player fires a shot at the computer's board.
    public static void playerTurn() {
        Scanner input = new Scanner(System.in);
        boolean continueTurn = true;
        while (continueTurn) {
            System.out.println("\nYOUR TURN");
            int x = -1, y = -1;
            boolean validInput = false;
            // Get a valid coordinate from the player.
            while (!validInput) {
                System.out.print("Enter X coordinate for your shot: ");
                x = input.nextInt();
                System.out.print("Enter Y coordinate for your shot: ");
                y = input.nextInt();
                if (x < 0 || x >= numRows || y < 0 || y >= numCols) {
                    System.out.println("Coordinates out of bounds. Try again.");
                } else {
                    validInput = true;
                }
            }

            // Process the shot on the computer's board.
            // Check if the cell contains a ship marker ("5", "4", "3", or "2").
            if (computerGrid[x][y].equals("5") || computerGrid[x][y].equals("4") ||
                    computerGrid[x][y].equals("3") || computerGrid[x][y].equals("2")) {
                System.out.println("Boom! You hit a ship at (" + x + ", " + y + ")!");
                computerGrid[x][y] = "X"; // Mark the hit.
                
                // Continue the turn since the player hit.
            } else if (computerGrid[x][y].equals(" ")) {
                System.out.println("You missed.");
                computerGrid[x][y] = "-";
                continueTurn = false; // End turn on a miss.
            } else {
                System.out.println("You already shot here. Try a different coordinate.");
            }

            printComputerBoard();
        }
    }

    // Computer's turn
    public static void computerTurn() {
        System.out.println("\nCOMPUTER'S TURN");
        int x, y;
        // Use candidate moves from previous hits if available.
        if (!targetList.isEmpty()){
            int[] candidate = targetList.remove(0);
            x = candidate[0];
            y = candidate[1];
            while(isAlreadyGuessed(playerGrid, x, y) && !targetList.isEmpty()){
                candidate = targetList.remove(0);
                x = candidate[0];
                y = candidate[1];
            }
            if(isAlreadyGuessed(playerGrid, x, y)){
                int[] randomCoord = getRandomCoordinate(playerGrid);
                x = randomCoord[0];
                y = randomCoord[1];
            }
        } else {
            int[] randomCoord = getRandomCoordinate(playerGrid);
            x = randomCoord[0];
            y = randomCoord[1];
        }

        // Process shot on player's board.
        if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") ||
                playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2")) {
            System.out.println("Computer hit your ship at (" + x + ", " + y + ")!");
            playerGrid[x][y] = "X";
            addCandidates(x, y);
        } else if (playerGrid[x][y].equals(" ")) {
            System.out.println("Computer missed at (" + x + ", " + y + ").");
            playerGrid[x][y] = "-";
        } else {
            System.out.println("Computer already shot at (" + x + ", " + y + ").");
        }

        printPlayerBoard();
    }

    public static int[] getRandomCoordinate(String[][] board) {
        int x, y;
        Random random = new Random();
        do {
            x = random.nextInt(numRows);
            y = random.nextInt(numCols);
        } while(isAlreadyGuessed(board, x, y));
        return new int[]{x, y};
    }

    // Check if a cell on a given board has already been guessed.
    public static boolean isAlreadyGuessed(String[][] board, int x, int y) {
        return board[x][y].equals("X") || board[x][y].equals("-");
    }

    // Add adjacent candidate cells for the computer's targeting AI.
    public static void addCandidates(int x, int y) {
        if (x > 0 && !isAlreadyGuessed(playerGrid, x-1, y))
            targetList.add(new int[]{x-1, y});
        if (x < numRows - 1 && !isAlreadyGuessed(playerGrid, x+1, y))
            targetList.add(new int[]{x+1, y});
        if (y > 0 && !isAlreadyGuessed(playerGrid, x, y-1))
            targetList.add(new int[]{x, y-1});
        if (y < numCols - 1 && !isAlreadyGuessed(playerGrid, x, y+1))
            targetList.add(new int[]{x, y+1});
    }

    // game over display.
    public static void gameOver() {
        System.out.println("\nGame Over");
        // check if all ships on one board are sunk.
        if(playerShips > 0 && computerShips <= 0)
            System.out.println("Hooray! You won the battle :)");
        else if(computerShips > 0 && playerShips <= 0)
            System.out.println("Sorry, you lost the battle");
        else
            System.out.println("Game ended.");
    }
}
