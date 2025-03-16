import java.util.*;

// Harold:
// What's up guys. The code has been completely revamped due to the game not being complete.
// The main problem the code faced was playing the whole game on one board. Now the boards are split using two
// different grids.

// Further changes below:
// - Turns are now no longer locked to alternation. If a ship is struck, the player may go again.
// - Added different ship sizes. Specifically 5, 4, 3, 2.
// - While adding your ships to the table, you now see live updates of your map.

public class BattleShips {
    public static int numRows = 10;
    public static int numCols = 10;
    public static int playerShips;
    public static int computerShips;
    // We have to save because the memory state of the computer needs to remember where exactly it was and what it was doing
    // so if it loses a turn - it can come back and rememebr this is the original bomb that I dropped.
    //public int[][] computerLastHit =[0][0];
    //public int[] hit1 = [0,0];
    //public bool computerIsLater = false; This is for when the comptuer is trying to remember whether it is going left right (true) or top down (false)
    //public hitcounter = 0; 
    // public bool keepRight = false; for when the go left stategy doesn't work - go all the way right
    // public bool keepTop = false; for when the go bottom strat blah blah
    // public bool hit = false;



//Implementation of pseudo below
   public class ComputerMemory {
        private int[][] computerLastHit = new int[1][2]; // Stores the last hit position
        private int[] hit1 = new int[2]; // First hit position in a sequence
        private boolean computerIsLateral = false; // Determines if the computer searches left-right or top-down
        private int hitCounter = 0; // Tracks consecutive hits
        private boolean keepRight = false; // Determines if the computer should keep searching right
        private boolean keepTop = false; // Determines if the computer should keep searching upward
        private boolean hit = false; // Tracks whether a hit was made
        // Constructor
        public ComputerMemory() {
            computerLastHit[0][0] = -1; // Default invalid coordinate
            computerLastHit[0][1] = -1;
            hit1[0] = -1;
            hit1[1] = -1;
        }
        // Getter and Setter Methods
        public int[][] getComputerLastHit() {
            return computerLastHit;
        }
        public void setComputerLastHit(int x, int y) {
            computerLastHit[0][0] = x;
            computerLastHit[0][1] = y;
        }
        public int[] getHit1() {
            return hit1;
        }
        public void setHit1(int x, int y) {
            hit1[0] = x;
            hit1[1] = y;
        }
        public boolean isComputerLateral() {
            return computerIsLateral;
        }
        public void setComputerLateral(boolean lateral) {
            this.computerIsLateral = lateral;
        }
        public int getHitCounter() {
            return hitCounter;
        }
        public void incrementHitCounter() {
            this.hitCounter++;
        }
        public void resetHitCounter() {
            this.hitCounter = 0;
        }
        public boolean isKeepRight() {
            return keepRight;
        }
        public void setKeepRight(boolean keepRight) {
            this.keepRight = keepRight;
        }
        public boolean isKeepTop() {
            return keepTop;
        }
        public void setKeepTop(boolean keepTop) {
            this.keepTop = keepTop;
        }
        public boolean isHit() {
            return hit;
        }
        public void setHit(boolean hit) {
            this.hit = hit;
        }
    }    
// END 
    // Added feature where each player has their own board. The original code only used one board.
    public static String[][] playerGrid = new String[numRows][numCols];
    public static String[][] computerGrid = new String[numRows][numCols];

    // This will be the list of options the AI considers stored in an ArrayList.
    public static ArrayList<int[]> targetList = new ArrayList<>();

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
                System.out.print("This ship is " + shipLength + " tiles long. Enter its Y coordinate: ");
                int x = input.nextInt();
                System.out.print("This ship is " + shipLength + " tiles long. Enter its X coordinate: ");
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
                //playerTurn();
                
                System.out.println("Boom! You hit a ship at (" + x + ", " + y + ")!");
                computerGrid[x][y] = "X"; // Mark the hit.

                // Continue the turn since the player hit.
            } else if (computerGrid[x][y].equals(" ")) {
                System.out.println("You missed.");
                computerGrid[x][y] = "O";
                continueTurn = false; // End turn on a miss.
            } else {
                System.out.println("You already shot here. Try a different coordinate.");
            }

            printComputerBoard();
        }
    }

    // Computer's turn
    public static void computerTurn() {
        // while(hit)
        //{
        //everything
        //}
        // change to do while please
        //Might be a good idea to change this to a do while loop
        System.out.println("\nCOMPUTER'S TURN");
        int x, y;
        // Use candidate moves from previous hits if available.
        //if hit is false
        if (!targetList.isEmpty()){
            int[] candidate = targetList.remove(0);
            x = candidate[0];
            y = candidate[1];
            while(isAlreadyGuessed(playerGrid, x, y) && !targetList.isEmpty())
            {
                candidate = targetList.remove(0);
                x = candidate[0];
                y = candidate[1];
            }
            if(isAlreadyGuessed(playerGrid, x, y))
            {
                // I dont know what to do here
                // run a million tests based on random coords hitting or not hitting
                // choose the one that fared best
                //
                int[] randomCoord = getRandomCoordinate(playerGrid);
                x = randomCoord[0];
                y = randomCoord[1];
            }
        } 
        else 
        {
            int[] randomCoord = getRandomCoordinate(playerGrid);
            x = randomCoord[0];
            y = randomCoord[1];
        }
        //hit is false^

        // if counter == 1 ;
        // 
        // then
        // hit1.y
        // going to the left means -1 for the y IE y=  hit1[0][0];
        // hit1[0][0]-1 && hit1[0][0]-1 > 0
        //
        // x stays the same
        //
        // if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") || playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2"))
        // then
        //  comptuerIsLateral = true
        //  playerGrid[x][y] = "X";
        // else 
        //  computerIsLater = false

        //
        // if (counter < 5 && counter > 1 && computerIsLater && !isDead)
        // then
        // hit1.y (which is the left or right)
        // y = y-1;
        // x stays the same 
        // if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") || playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2"))
        // then 
        // playerGrid[x][y] = "X";
        // 
        // else (as in going left no longer works)
        // goRight = true
        // so here we lose a turn
        // come back now with the following public vars changed - counter = (however many times we hit left), goRight = true, computerIsLater = true
        // isDead == false
        // so here we loop right
        // while(counter<5 && goRight && !isDead)
        // hit1.y (whatever the y is for the original hit + 1 (unless it hits a wall which then isDead == true)
        // y = hit1.y
        // y + 1
        // if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") || playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2"))
        // (we hit something keeps going)
        // playerGrid[x][y] = "X";
        // else
        // isDead = true; (this exits the loop)
        // hit = false;
        // counter = 0;
        // computerIsLateral = false;
        // go back to random hit choices/
        // once it exits
        // isDead

        // Process shot on player's board.
        if (playerGrid[x][y].equals("5") || playerGrid[x][y].equals("4") ||
                playerGrid[x][y].equals("3") || playerGrid[x][y].equals("2")) {
            //
            // save these x,y right here because it means it hit something -
            // hit1[][] = [x][y]; 
            // counter++;
            // hit true
            System.out.println("Computer hit your ship at (" + x + ", " + y + ")!");
            playerGrid[x][y] = "X";
            addCandidates(x, y);
        } else if (playerGrid[x][y].equals(" ")) {
            System.out.println("Computer missed at (" + x + ", " + y + ").");
            // hit = false
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

    // int killedships = 0;
    // if(isDead)
    //{
    //killedships++;
    //}
    // if(killedships == 4)
    //{gameover}
    //
    // Taking the last ship hit and making a search
    // save array of the last hit
    //classify the ship as (isLateral) or !Lateral
    //we build a series of x,y coordinates
    //From that  - we can make a distinction on whether something is to the left or right

    //if there is a hit next to the player grid on the left - 
    //then [hit1.x-1,hit1.y] is the next hit UNTIL
    // next hit [hit1.x-i,hit1.y] == a miss or -
    //
    //if there is a hit next to the player grid on the right - 
    //Then 
    //then [hit1.x+1,hit1.y] is the next hit UNTIL
    // next hit [hit1.x+i,hit1.y] == a miss or -
    //
    //save a hit = hit1[][] = [
    //
//x = randomCoord[0],
//y = randomCoord[1]];
    //

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
