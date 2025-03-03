import random
import csv

BOARD_SIZE = 10
SHIPS = [5, 4, 3, 3, 2]  # Total ship cells = 17

class BattleshipGame:
    def __init__(self):
        random.seed(None)  # Ensure full randomness
        self.player_board = self.place_ships()
        self.ai_board = self.place_ships()
        self.player_hits = set()
        self.ai_hits = set()
        self.ai_targets = []  # AI's list of follow-up targets after a hit
        self.ai_successful_hits = []  # List to store AI successful hits coordinates
        self.player_successful_hits = []  # List to store Player successful hits coordinates

    def place_ships(self):
        """Randomly places ships with better variation."""
        board = set()
        for ship in SHIPS:
            placed = False
            while not placed:
                x, y = random.randint(0, BOARD_SIZE - 1), random.randint(0, BOARD_SIZE - 1)
                direction = random.choice(["H", "V"])
                if self.valid_placement(board, x, y, ship, direction):
                    for i in range(ship):
                        board.add((x + (i if direction == "H" else 0), y + (i if direction == "V" else 0)))
                    placed = True
        return board

    def valid_placement(self, board, x, y, ship, direction):
        """Ensures ships are placed without overlap."""
        for i in range(ship):
            if direction == "H":
                if (x + i, y) in board or x + i >= BOARD_SIZE:
                    return False
            else:
                if (x, y + i) in board or y + i >= BOARD_SIZE:
                    return False
        return True

    def play_game(self):
        """Runs a single game simulation with more randomness."""
        player_moves, ai_moves = 0, 0
        while len(self.player_hits) < len(self.ai_board) and len(self.ai_hits) < len(self.player_board):
            # Player's turn: Random or semi-strategic shooting
            self.player_turn()
            player_moves += 1

            # AI's turn: More strategic shooting
            self.ai_turn()
            ai_moves += 1

        # Determining the winner based on successful hits
        if len(self.player_hits) == len(self.ai_board):
            winner = "Player"
        else:
            winner = "AI"
        
        # Return all relevant data for recording
        return winner, player_moves, ai_moves, self.ai_successful_hits, self.player_successful_hits

    def player_turn(self):
        """Player fires randomly with 80% chance of pure random shooting."""
        if random.random() < 0.8:  # 80% chance of purely random shot
            while True:
                x, y = random.randint(0, BOARD_SIZE - 1), random.randint(0, BOARD_SIZE - 1)
                if (x, y) not in self.player_hits:
                    self.player_hits.add((x, y))
                    if (x, y) in self.ai_board:
                        self.player_successful_hits.append((x, y))
                    break
        else:  
            # 20% chance of smarter targeting (re-firing near known ship areas)
            self.player_hits.add(random.choice(list(self.ai_board - self.player_hits)))

    def ai_turn(self):
        """AI shoots at random, but uses smarter strategies to increase chances of hits."""
        if self.ai_targets:
            random.shuffle(self.ai_targets)
            x, y = self.ai_targets.pop()
        else:
            while True:
                x, y = random.randint(0, BOARD_SIZE - 1), random.randint(0, BOARD_SIZE - 1)
                if (x, y) not in self.ai_hits:
                    break

        self.ai_hits.add((x, y))

        # If AI hits a ship, add adjacent spots as potential targets and record the successful hit
        if (x, y) in self.player_board:
            self.ai_successful_hits.append((x, y))
            possible_targets = [(x+dx, y+dy) for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]
                                if 0 <= x+dx < BOARD_SIZE and 0 <= y+dy < BOARD_SIZE
                                and (x+dx, y+dy) not in self.ai_hits]
            random.shuffle(possible_targets)
            self.ai_targets.extend(possible_targets)

# Run multiple simulations with more randomness
def run_simulations(num_games=1000, output_file="battleship_randomized.csv"):
    results = []
    for i in range(num_games):
        game = BattleshipGame()
        winner, player_moves, ai_moves, ai_hits, player_hits = game.play_game()
        # Record all the required data
        results.append([i + 1, winner, player_moves, ai_moves, ai_hits, player_hits])
        print(f"Game {i+1}: Winner = {winner}, Player Moves = {player_moves}, AI Moves = {ai_moves}, "
              f"AI Successful Hits = {len(ai_hits)}, Player Successful Hits = {len(player_hits)}")

    # Save to CSV with the updated columns
    with open(output_file, "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["Game ID", "Winner", "Player Moves", "AI Moves", "AI Successful Hits", "Player Successful Hits"])
        writer.writerows(results)

    print(f"Simulation complete. Data saved to {output_file}")

# Run 1000 simulations
run_simulations(1000)




import pandas as pd

# Read the CSV file
df = pd.read_csv("battleship_randomized.csv")

# Set pandas options to display all rows and columns
pd.set_option("display.max_columns", None)  # Show all columns

# Print the full DataFrame
print(df)

