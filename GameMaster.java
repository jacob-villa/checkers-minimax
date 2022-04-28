import java.util.Scanner;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;

public class GameMaster 
{
    private int rows;
    private int cols;
    private Scanner scan;
    private Board gameboard;
    private Checker selectedPiece;
    private int currentTurn; // 1 - black, 2 - red
    private ArrayList<Move> validMoves;
    private double nodeCounter; 
    private Board bestState;
    private final int origDepth = 3;
    private int traversals;

    public GameMaster(Scanner scan, int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        this.scan = scan;
        this.currentTurn = 2;
        this.validMoves = new ArrayList<Move>();

        this.gameboard = new Board(scan, rows, cols);
    }

    public void resetGame()
    {
        this.selectedPiece = null;
        this.gameboard = new Board(scan, rows, cols);
        this.currentTurn = 2;
        this.validMoves.clear();

        this.playGame();
    }

    public void playGame()
    {
        System.out.println();
        System.out.println("Starting Checkers game.");
        int selectedRow = 0;
        int selectedCol = 0;
        char c;

        do
        {
            this.gameboard.displayBoard();
            System.out.println();

            if (this.currentTurn == 1)
            {
                this.nodeCounter = 0;
                this.traversals = 0;
                System.out.println("BLACK (COMPUTER) TURN");
                this.minimax(this.gameboard, origDepth, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                this.computerMove(bestState);
                System.out.println("Number of nodes visited: " + ((int)this.nodeCounter));
                System.out.println("Average branching factor: " + (this.nodeCounter / this.traversals));
            }
            else
            {
                do
                {
                    do
                    {
                        // Input for selecting Checker
                        do
                        {
                            if (this.currentTurn == 2)
                            System.out.println("RED TURN:");
                            else if (this.currentTurn == 1)
                                System.out.println("BLACK TURN:");    
                            
                            do
                            {
                                System.out.print("Enter row of Checker to be selected [1 - 8]: ");
                                selectedRow = scan.nextInt();
                            }
                            while (selectedRow <= 0 || selectedRow >= 9);
                            do
                            {
                                System.out.print("Enter column of Checker to be selected [1 - 8]: ");
                                selectedCol = scan.nextInt();
                            }
                            while (selectedCol <= 0 || selectedCol >= 9);
                        }
                        // selectPiece will return false when side == 0 or side != currentTurn
                        while (!selectPiece(selectedRow - 1, selectedCol - 1));

                        // Once selectPiece returns true, the given piece will have been selected and validMoves generated
                    }
                    // If no valid moves for selected piece, selectedPiece becomes null and ask for new selection of piece
                    while (!printValidMoves());
                    
                    // Asking for input for move to make
                    do
                    {
                        System.out.print("Enter row of move to make [1 - 8]: ");
                        selectedRow = scan.nextInt();
                    }
                    while (selectedRow <= 0 || selectedRow >= 9);
                    do
                    {
                        System.out.print("Enter column of move to make [1 - 8]: ");
                        selectedCol = scan.nextInt();
                    }
                    while (selectedCol <= 0 || selectedCol >= 9);
                }
                // If move is invalid, will set selectedPiece to null and ask for selection of piece again
                while (!selectPiece(selectedRow - 1, selectedCol - 1));

            }
        }
        while(!isGameOver());

        System.out.print("Play again? [Y/N]: ");
        c = scan.next().charAt(0);

        if (c == 'Y')
            this.resetGame();
    }

    public boolean selectPiece(int row, int col)
    {
        if (!Objects.isNull(this.selectedPiece))
        {
            if (!this.movePiece(row, col))
                this.selectedPiece = null;
            else
                return true;
        }
    
        Checker piece = this.gameboard.getPiece(row, col);
        if (piece.getSide() != 0 && piece.getSide() == this.currentTurn)
        {
            this.selectedPiece = piece;
            // Initializing validMoves based on the current selected piece
            this.validMoves = this.gameboard.getValidMoves(piece);
            System.out.println("Selected piece in (" + (row + 1) + ", " + (col + 1) + ").");
            return true;
        }
        
        return false;
    }

    public boolean movePiece(int row, int col) 
    {
        Checker piece = this.gameboard.getPiece(row, col);
        Move m = new Move(row, col);

        if (!Objects.isNull(this.selectedPiece) && piece.getSide() == 0 && this.validMoves.contains(m))
        {
            // Moving the piece to specified row and col
            this.gameboard.movePiece(this.selectedPiece, row, col);

            // Getting all pieces skipped when making the specific Move m
            ArrayList<Checker> skippedCheckers = new ArrayList<Checker>();
            skippedCheckers.addAll(this.validMoves.get(this.validMoves.indexOf(m)).getCapturedCheckers());

            if (!skippedCheckers.isEmpty())
            {
                for (int i = 0; i < skippedCheckers.size(); i++)
                {
                    // Set side of the captured checker to 0
                    skippedCheckers.get(i).setSide(0);
                    // If red checker was captured
                    if (skippedCheckers.get(i).getSide() == 2)
                        this.gameboard.decrementRedCheckers();
                    // If black checker was captured
                    else if (skippedCheckers.get(i).getSide() == 1)
                        this.gameboard.decrementBlackCheckers();
                    
                    // Set side of the captured checker in the board[][] to 0
                    this.gameboard.getPiece(skippedCheckers.get(i).getRow(), skippedCheckers.get(i).getCol()).setSide(0);
                }
            }
            
            this.nextTurn();
        }    
        else
            return false;
        
        return true;
    }

    public void nextTurn()
    {
        this.validMoves.clear();

        if (this.currentTurn == 1)
            this.currentTurn++;
        else if (this.currentTurn == 2)
            this.currentTurn--;
    }

    public boolean isGameOver()
    {
        if (this.gameboard.getBlackCheckers() <= 0)
        {
            System.out.println("Red wins the game.");
            return true;
        }
        else if (this.gameboard.getRedCheckers() <= 0)
        {
            System.out.println("Black wins the game.");
            return true;
        }
        
        return false;
    }

    public boolean printValidMoves()
    {
        if (!this.validMoves.isEmpty())
        {
            System.out.print("Valid Moves: ");

            for (int i = 0; i < this.validMoves.size(); i++)
                System.out.print("(" + (this.validMoves.get(i).getRow() + 1) + ", " + (this.validMoves.get(i).getCol() + 1) + ") ");
                
            System.out.println();

            return true;
        }
        else
            System.out.println("No valid moves available for selected piece. Please select another piece.");

        return false;
    }

    public void computerMove(Board board)
    {
        this.gameboard = board;
        this.nextTurn();
    }

    // Given an arraylist of Board states, sorts the arraylist in either ascending or descending order
    // Uses bubble sort, sorting is based on heuristic value of board state
    public void orderMoves(ArrayList<Board> moves, boolean isDescending)
    {
        if (isDescending)
        {
            for (int i = 0; i < moves.size() - 1; i++)
            {
                for (int j = 0; j < moves.size() - i - 1; j++)
                {
                    if (moves.get(j).computeHeuristic() < moves.get(j + 1).computeHeuristic())
                        Collections.swap(moves, j, j + 1);
                }
            }
        }
        else
        {
            for (int i = 0; i < moves.size() - 1; i++)
            {
                for (int j = 0; j < moves.size() - i - 1; j++)
                {
                    if (moves.get(j).computeHeuristic() > moves.get(j + 1).computeHeuristic())
                        Collections.swap(moves, j, j + 1);
                }
            }
        }
    }

    public double minimax(Board state, int depth, boolean isMaximizer, double alphaVal, double betaVal)
    {
        // Base case
        if (depth == 0 || state.getRedCheckers() <= 0 || state.getBlackCheckers() <= 0)
        {
            return state.computeHeuristic();
        }

        if (isMaximizer)
        {
            double value = Double.NEGATIVE_INFINITY;
            ArrayList<Board> allMoves = new ArrayList<Board>();
            allMoves = this.getAllMoves(state, 1);

            // Implementation of move ordering by sorting the possible moves by heuristic value
            // Sorting states in descending order of heuristic values
            orderMoves(allMoves, true);
            
            this.traversals++;
            
            // Recursively checking each possible state
            for (int i = 0; i < allMoves.size(); i++)
            {
                // Recursive call
                value = Math.max(value, minimax(allMoves.get(i), depth - 1, false, alphaVal, betaVal));

                // Setting best state/move when depth is orig depth
                alphaVal = Math.max(value, alphaVal);
                if (alphaVal == value && depth == this.origDepth)
                    this.bestState = allMoves.get(i);

                // Incrementing node counter after checking the heuristic value of the state
                this.nodeCounter++;

                // Pruning step, stop going through moves if the current best is better than the betaVal passed
                if (value >= betaVal)
                {
                    break;
                }           
            }

            return value;
        }

        else 
        {  
            double value = Double.POSITIVE_INFINITY;
            ArrayList<Board> allMoves = new ArrayList<Board>();
            allMoves = this.getAllMoves(state, 2);

            // Implementation of move ordering by sorting the possible moves by heuristic value
            // Sorting in ascending order of heuristic values
            orderMoves(allMoves, false);

            this.traversals++;

            // Recursively checking each possible state
            for (int i = 0; i < allMoves.size(); i++)
            {
                // Recursive call
                value = Math.min(value, minimax(allMoves.get(i), depth - 1, true, alphaVal, betaVal));

                // Setting best state/move when depth is orig depth
                betaVal = Math.min(value, alphaVal);
                if (betaVal == value && depth == this.origDepth)
                    this.bestState = allMoves.get(i);

                // Increment node counter
                this.nodeCounter++;

                // Pruning step
                if (value <= alphaVal)
                {
                    break;
                }
                       
            }

            return value;
        }
    }

    // Tries Move m on Checker piece in the temporary board state passed
    // Returns the resulting board state after making move m
    public Board tryMove(Checker piece, Move m, Board state)
    {
        // Moving the piece in the temporary board/state
        state.movePiece(piece, m.getRow(), m.getCol());

        // Deleting respective skipped pieces in the temp board and decrementing piece count
        if (!m.getCapturedCheckers().isEmpty())
        {
            for (int i = 0; i < m.getCapturedCheckers().size(); i++)
            {
                // Only setting the piece in the temporary board to side = 0
                state.getPiece(m.getCapturedCheckers().get(i).getRow(), m.getCapturedCheckers().get(i).getCol()).setSide(0);
                if (m.getCapturedCheckers().get(i).getSide() == 2)
                    state.decrementRedCheckers();
                else if (m.getCapturedCheckers().get(i).getSide() == 1)
                    state.decrementBlackCheckers();
            }
        }

        return state;
    }

    // Takes all pieces of the given side, gets all the valid moves for each side, tries the moves, adds to moves
    // Returns an arraylist of Board objects representing the various states after making each possible move
    public ArrayList<Board> getAllMoves(Board currentState, int side)
    {
        ArrayList<Board> moves = new ArrayList<Board>();
        ArrayList<Checker> pieces = new ArrayList<Checker>();
        ArrayList<Move> validMoveList = new ArrayList<Move>();
        Board tempState, newState;
        Checker tempPiece;
        pieces = currentState.getAllCheckers(side);

        for (int i = 0; i < pieces.size(); i++)
        {
            validMoveList = currentState.getValidMoves(pieces.get(i));

            for (int j = 0; j < validMoveList.size(); j++)
            {
                tempState = new Board(currentState);
                tempPiece = tempState.getPiece(pieces.get(i).getRow(), pieces.get(i).getCol());
                newState = tryMove(tempPiece, validMoveList.get(j), tempState);
                moves.add(newState);
            }
        }

        return moves;
    }
}
