import java.util.*;

public class BattleShips {
    public static int numRows = 10;
    public static int numCols = 10;
    public static int playerShips;
    public static int computerShips;
    // We have to save because the memory state of the computer needs to remember where exactly it was and what it was doing
    // so if it loses a turn - it can come back and rememebr this is the original bomb that I dropped.
    // (The following commented pseudocode was implemented via the ComputerMemory inner class and our targeting logic.)
    //public int[][] computerLastHit =[0][0];
    //public int[] hit1 = [0,0];
    //public bool computerIsLater = false; This is for when the comptuer is trying to remember whether it is going left right (true) or top down (false)
    //public hitcounter = 0;
    // public bool keepRight = false; for when the go left stategy doesn't work - go all the way right
    // public bool keepTop = false; for when the go bottom strat blah blah
    // public bool hit = false;

    // Implementation of pseudo below
    public class ComputerMemory {
        private int[][] computerLastHit = new int[1][2]; // Stores the last hit position
        private int[] hit1 = new int[2]; // First hit position in a sequence
        private boolean computerIsLateral = false; // Determines if the computer searches left-right (true) or top-down (false)
        private int hitCounter = 0; // Tracks consecutive hits
        private boolean keepRight = false; // Determines if the computer should switch direction horizontally (or vertically if inverted)
        private boolean keepTop = false; // (Not used in this implementation)
        private boolean hit = false; // Tracks whether a hit was made in the current sequence

        // Constructor
        public ComputerMemory() {
            computerLastHit[0][0] = -1;
            computerLastHit[0][1] = -1;
            hit1[0] = -1;
            hit1[1] = -1;
        }
        public int[] getHit1() {
            return hit1;
        }
        public void setHit1(int x, int y) {
            hit1[0] = x;
            hit1[1] = y;
        }
        public int getHitCounter() {
            return hitCounter;
        }
        public void incrementHitCounter() {
            hitCounter++;
        }
        public void resetHitCounter() {
            hitCounter = 0;
        }
        public boolean isComputerLateral() {
            return computerIsLateral;
        }
        public void setComputerLateral(boolean lateral) {
            computerIsLateral = lateral;
        }
        public boolean isKeepRight() {
            return keepRight;
        }
        public void setKeepRight(boolean kr) {
            keepRight = kr;
        }
        public void resetMemory() {
            hit1[0] = -1;
            hit1[1] = -1;
            resetHitCounter();
            computerIsLateral = false;
            keepRight = false;
        }
    }

    // Added feature where each player has their own board. The original code only used one board.
    public static String[][] playerGrid = new String[numRows][numCols];
    public static String[][] computerGrid = new String[numRows][numCols];

    // This will be the list of options the AI considers stored in an ArrayList.
    public static ArrayList<int[]> targetList = new ArrayList<>();

    // Create a static instance of ComputerMemory to hold the CPU’s targeting state.
    public static ComputerMemory cpuMemory = new BattleShips().new ComputerMemory();

    public static void main(String[] args) {
        System.out.println("**** WELCOME TO BATTLESHIP VI! ****");
        System.out.println("Loading...\n");
        printEmptyBoard();
        initBoards();

        deployPlayerShips();
        deployComputerShips();
        printPlayerBoard();
        // The game loops until one side loses all of their ships.
        do {
            playerTurn();
            computerTurn();
        } while (playerShips > 0 && computerShips > 0);

        gameOver();
    }

    public static void printEmptyBoard() {
        System.out.println("  0123456789");
        for (int i = 0; i < numRows; i++) {
            System.out.println(i + "|          |" + i + "\n");
        }
        System.out.println("  0123456789\n");
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
                // Hide unhit ship positions: if the cell has "5", "4", "3", or "2", print a blank.
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
                System.out.print("This ship is " + shipLength + " tiles long. Enter its Y coordinate: ");
                int x = input.nextInt();
                System.out.print("This ship is " + shipLength + " tiles long. Enter its X coordinate: ");
                int y = input.nextInt();
                System.out.print("Enter orientation (H for horizontal, V for vertical): ");
                char orientation = input.next().charAt(0);
                valid = true;

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
            if (computerGrid[x][y].equals("5") || computerGrid[x][y].equals("4") ||
                    computerGrid[x][y].equals("3") || computerGrid[x][y].equals("2")) {
                String marker = computerGrid[x][y];
                System.out.println("Boom! You hit a ship at (" + x + ", " + y + ")!");
                computerGrid[x][y] = "X";
                // If the ship is sunk, we decrement the computer's ship count.
                if (isShipSunk(marker, computerGrid)) {
                    System.out.println("You sunk the computer's ship of length " + marker + "!");
                    computerShips--;
                }
            } else if (computerGrid[x][y].equals(" ")) {
                System.out.println("You missed.");
                computerGrid[x][y] = "O";
                continueTurn = false;
            } else {
                System.out.println("You already shot here. Try a different coordinate.");
            }
            printComputerBoard();
        }
    }

    // Computer's turn; CPU fires shots (and loops its turn if it hits or chooses an already-guessed cell).
    public static void computerTurn() {
        System.out.println("\nCOMPUTER'S TURN");
        boolean continueTurn;
        do {
            int x = -1, y = -1;
            boolean shotChosen = false;

            // Use targeting memory if a hit sequence is in progress.
            if (cpuMemory.getHit1()[0] != -1) {
                if (cpuMemory.getHitCounter() == 1) {
                    // Try left of the first hit
                    int candidateY = cpuMemory.getHit1()[1] - 1;
                    if (candidateY >= 0 && !isAlreadyGuessed(playerGrid, cpuMemory.getHit1()[0], candidateY)) {
                        x = cpuMemory.getHit1()[0];
                        y = candidateY;
                        shotChosen = true;
                    } else {
                        // If left fails, try right.
                        candidateY = cpuMemory.getHit1()[1] + 1;
                        if (candidateY < numCols && !isAlreadyGuessed(playerGrid, cpuMemory.getHit1()[0], candidateY)) {
                            x = cpuMemory.getHit1()[0];
                            y = candidateY;
                            shotChosen = true;
                        }
                    }
                } else if (cpuMemory.getHitCounter() > 1) {
                    if (cpuMemory.isComputerLateral()) { // horizontal search
                        if (!cpuMemory.isKeepRight()) {
                            int candidateY = cpuMemory.getHit1()[1] - cpuMemory.getHitCounter();
                            if (candidateY >= 0 && !isAlreadyGuessed(playerGrid, cpuMemory.getHit1()[0], candidateY)) {
                                x = cpuMemory.getHit1()[0];
                                y = candidateY;
                                shotChosen = true;
                            } else {
                                cpuMemory.setKeepRight(true);
                            }
                        }
                        if (!shotChosen && cpuMemory.isKeepRight()) {
                            int candidateY = cpuMemory.getHit1()[1] + cpuMemory.getHitCounter();
                            if (candidateY < numCols && !isAlreadyGuessed(playerGrid, cpuMemory.getHit1()[0], candidateY)) {
                                x = cpuMemory.getHit1()[0];
                                y = candidateY;
                                shotChosen = true;
                            } else {
                                cpuMemory.resetMemory();
                            }
                        }
                    } else { // vertical search
                        if (!cpuMemory.isKeepRight()) { // using keepRight for vertical switch as well
                            int candidateX = cpuMemory.getHit1()[0] - cpuMemory.getHitCounter();
                            if (candidateX >= 0 && !isAlreadyGuessed(playerGrid, candidateX, cpuMemory.getHit1()[1])) {
                                x = candidateX;
                                y = cpuMemory.getHit1()[1];
                                shotChosen = true;
                            } else {
                                cpuMemory.setKeepRight(true);
                            }
                        }
                        if (!shotChosen && cpuMemory.isKeepRight()) {
                            int candidateX = cpuMemory.getHit1()[0] + cpuMemory.getHitCounter();
                            if (candidateX < numRows && !isAlreadyGuessed(playerGrid, candidateX, cpuMemory.getHit1()[1])) {
                                x = candidateX;
                                y = cpuMemory.getHit1()[1];
                                shotChosen = true;
                            } else {
                                cpuMemory.resetMemory();
                            }
                        }
                    }
                }

                if (!shotChosen) {
                    if (!targetList.isEmpty()){
                        int[] candidate = targetList.remove(0);
                        x = candidate[0];
                        y = candidate[1];
                        shotChosen = true;
                    } else {
                        int[] randomCoord = getRandomCoordinate(playerGrid);
                        x = randomCoord[0];
                        y = randomCoord[1];
                        shotChosen = true;
                    }
                }
            } else { // No targeting memory, choose from candidate list or random.
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
            }

            // Process the shot on player's board.
            if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") ||
                    playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2")) {
                String marker = playerGrid[x][y];
                System.out.println("Computer hit your ship at (" + x + ", " + y + ")!");
                playerGrid[x][y] = "X";
                // If ship is sunk, announce and reset targeting memory.
                if (isShipSunk(marker, playerGrid)) {
                    System.out.println("Computer sunk your ship of length " + marker + "!");
                    playerShips--;
                    cpuMemory.resetMemory();
                } else {
                    if (cpuMemory.getHit1()[0] == -1) {
                        cpuMemory.setHit1(x, y);
                        cpuMemory.resetHitCounter();
                        cpuMemory.incrementHitCounter();
                    } else {
                        cpuMemory.incrementHitCounter();
                        if (cpuMemory.getHitCounter() == 2) {
                            if (x == cpuMemory.getHit1()[0]) {
                                cpuMemory.setComputerLateral(true);
                            } else {
                                cpuMemory.setComputerLateral(false);
                            }
                        }
                    }
                    addCandidates(x, y);
                }
                continueTurn = true;
            } else if (playerGrid[x][y].equals(" ")) {
                System.out.println("Computer missed at (" + x + ", " + y + ").");
                playerGrid[x][y] = "-";
                cpuMemory.resetMemory();
                continueTurn = false;
            } else {
                System.out.println("Computer already shot at (" + x + ", " + y + "). Trying again...");
                continueTurn = true;
            }

            printPlayerBoard();
        } while (continueTurn && playerShips > 0);
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

    // will return true if no cell on the board contains the ship marker.
    public static boolean isShipSunk(String marker, String[][] board) {
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                if (board[i][j].equals(marker)) {
                    return false;
                }
            }
        }
        return true;
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
