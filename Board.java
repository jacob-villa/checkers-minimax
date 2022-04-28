import java.util.Scanner;
import java.util.ArrayList;

public class Board 
{
    private int rows;
    private int cols;
    private Checker[][] board;
    private int redCheckers;
    private int blackCheckers;
    private int redKings;
    private int blackKings;

    public Board(Scanner scan, int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        redCheckers = 12;
        blackCheckers = 12;
        redKings = 0;
        blackKings = 0;

        // Initializing board pieces
        board = new Checker[rows][cols];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                // Default side is 0 for empty square
                board[i][j] = new Checker(i, j, 0);
                
                if (j % 2 == (i + 1) % 2)
                {
                    if (i < 3)
                        board[i][j].setSide(1);
                    else if (i > 4)
                        board[i][j].setSide(2);
                }

            }
        }
    }

    // Copy constructor used to create a board object copy by passing the original board
    public Board(Board source)
    {
        this.rows = source.getRows();
        this.cols = source.getCols();
        this.redCheckers = source.getRedCheckers();
        this.blackCheckers = source.getBlackCheckers();
        this.redKings = source.getRedKings();
        this.blackKings = source.getBlackKings();
        this.board = new Checker[this.rows][this.cols];

        /*
        // Copying 2D array of Checker objects
        for (int i = 0; i < source.getBoard().length; i++)
        {
            this.board[i] = Arrays.copyOf(source.getBoard()[i], source.getBoard()[i].length);
        }
        */
        
        // If the above doesnt work in copying the board, use this manual copy 
        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.cols; j++)
            {
                this.board[i][j] = new Checker(source.getBoard()[i][j].getRow(), source.getBoard()[i][j].getCol(), source.getBoard()[i][j].getSide());
                if (source.getBoard()[i][j].checkIfKing())
                    this.board[i][j].makeKing();
            }
        }
        
    }

    public int getRows()
    {
        return this.rows;
    }

    public int getCols()
    {
        return this.cols;
    }

    public int getRedCheckers()
    {
        return this.redCheckers;
    }

    public int getBlackCheckers()
    {
        return this.blackCheckers;
    }

    public int getRedKings()
    {
        return this.redKings;
    }

    public int getBlackKings()
    {
        return this.blackKings;
    }

    public Checker[][] getBoard()
    {
        return this.board;
    }

    public void decrementRedCheckers()
    {
        this.redCheckers--;
    }

    public void decrementBlackCheckers()
    {
        this.blackCheckers--;
    }

    public void displayBoard()
    {
        System.out.println();
        for (int i = 0; i < rows; i++)
        {
            System.out.print(" | ");
            for (int j = 0; j < cols; j++)
            {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println((i + 1) + "| - | - | - | - | - | - | - | - |");
        }
        System.out.println("   1   2   3   4   5   6   7   8\n");
    }

    public Checker getPiece(int row, int col)
    {
        return board[row][col];
    }

    public boolean isValidCoord(int row, int col)
    {
        if (row >= 0 && row < this.rows && col >= 0 && col < this.cols)
            return true;
        
        return false;
    }

    public void movePiece(Checker piece, int targetRow, int targetCol)
    {
        // Swapping pieces in board[][]
        board[piece.getRow()][piece.getCol()] = board[targetRow][targetCol];
        board[piece.getRow()][piece.getCol()].move(piece.getRow(), piece.getCol());
        board[targetRow][targetCol] = piece;
        piece.move(targetRow, targetCol);
        
        // Make the piece a king if it hits the end of the board
        if (targetRow == rows - 1 || targetRow == 0)
        {
            if (!piece.checkIfKing())
            {
                piece.makeKing();

                if (piece.getSide() == 1)
                    this.blackKings++;
                else if (piece.getSide() == 2)
                    this.redKings++;
            }
        }        
    }

    public ArrayList<Move> getValidMoves(Checker piece)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        // Moving a red Checker
        if (piece.getSide() == 2)
        {
            ArrayList<Move> leftDiagonalMoves = new ArrayList<Move>();
            leftDiagonalMoves = traverseLeftDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, -1, new ArrayList<Move>(), new ArrayList<Checker>());
            ArrayList<Move> rightDiagonalMoves = new ArrayList<Move>();
            rightDiagonalMoves = traverseRightDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, -1, new ArrayList<Move>(), new ArrayList<Checker>());
            
            moves.addAll(leftDiagonalMoves);
            moves.addAll(rightDiagonalMoves);

            if (piece.checkIfKing())
            {
                moves.addAll(traverseLeftDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, 1, new ArrayList<Move>(), new ArrayList<Checker>()));
                moves.addAll(traverseRightDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, 1, new ArrayList<Move>(), new ArrayList<Checker>()));
            }
        }

        // Moving a black Checker
        if (piece.getSide() == 1)
        {
            ArrayList<Move> leftDiagonalMoves = new ArrayList<Move>();
            leftDiagonalMoves = traverseLeftDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, 1, new ArrayList<Move>(), new ArrayList<Checker>());
            ArrayList<Move> rightDiagonalMoves = new ArrayList<Move>();
            rightDiagonalMoves = traverseRightDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, 1, new ArrayList<Move>(), new ArrayList<Checker>());
            
            moves.addAll(leftDiagonalMoves);
            moves.addAll(rightDiagonalMoves);

            if (piece.checkIfKing())
            {
                moves.addAll(traverseLeftDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, -1, new ArrayList<Move>(), new ArrayList<Checker>()));
                moves.addAll(traverseRightDiagonal(piece, piece.getRow(), piece.getCol(), piece.getSide(), false, -1, new ArrayList<Move>(), new ArrayList<Checker>()));
            }
        }

        return moves;
    }

    public ArrayList<Move> traverseLeftDiagonal(Checker piece, int row, int col, int side, boolean madeCapture, int step, ArrayList<Move> moves, ArrayList<Checker> capturedCheckers)
    {
        int targetRow = row + step;
        int targetCol = col - 1;
        Checker targetPiece = new Checker(-1, -1, 0);
        try
        {
            targetPiece = this.board[targetRow][targetCol];
        }
        catch (Exception e)
        {

        }

        // If left diagonal contains a blank space, add it to possible moves
        if (isValidCoord(targetRow, targetCol) && targetPiece.getSide() == 0 && !madeCapture)
        {
            moves.add(new Move(targetRow, targetCol));
            moves.get(moves.size() - 1).setMovedPiece(piece);
        }
            
        // If left diagonal contains an enemy Checker
        else if (isValidCoord(targetRow, targetCol) && targetPiece.getSide() != 0 && targetPiece.getSide() != side && !capturedCheckers.contains(targetPiece))
        {
            targetRow += step;
            targetCol -= 1;

            if (isValidCoord(targetRow, targetCol))
                targetPiece = this.board[targetRow][targetCol];

            if (isValidCoord(targetRow, targetCol) && (targetPiece.getSide() == 0 || targetPiece == piece))
            {
                moves.add(new Move(targetRow, targetCol));
                moves.get(moves.size() - 1).getCapturedCheckers().addAll(capturedCheckers);
                moves.get(moves.size() - 1).getCapturedCheckers().add(this.board[targetRow - step][targetCol + 1]);
                moves.get(moves.size() - 1).setMovedPiece(piece);

                traverseLeftDiagonal(piece, targetRow, targetCol, side, true, step, moves, moves.get(moves.size() - 1).getCapturedCheckers());
                traverseRightDiagonal(piece, targetRow, targetCol, side, true, step, moves, moves.get(moves.size() - 1).getCapturedCheckers());
            }
            
        }

        return moves;
    }

    private ArrayList<Move> traverseRightDiagonal(Checker piece, int row, int col, int side, boolean madeCapture, int step, ArrayList<Move> moves, ArrayList<Checker> capturedCheckers)
    {
        int targetRow = row + step;
        int targetCol = col + 1;
        Checker targetPiece = new Checker(-1, -1, 0);
        try
        {
            targetPiece = this.board[targetRow][targetCol];
        }
        catch (Exception e)
        {

        }

        // If right diagonal contains a blank space, add it to possible moves
        if (isValidCoord(targetRow, targetCol) && targetPiece.getSide() == 0 && !madeCapture)
        {
            moves.add(new Move(targetRow, targetCol));
            moves.get(moves.size() - 1).setMovedPiece(piece);
        }
               
        // If right diagonal contains an enemy Checker
        else if (isValidCoord(targetRow, targetCol) && targetPiece.getSide() != 0 && targetPiece.getSide() != side && !capturedCheckers.contains(targetPiece))
        {
            targetRow += step;
            targetCol += 1;

            if (isValidCoord(targetRow, targetCol))
                targetPiece = this.board[targetRow][targetCol];

            if (isValidCoord(targetRow, targetCol) && (targetPiece.getSide() == 0 || targetPiece == piece))
            {
                moves.add(new Move(targetRow, targetCol));
                moves.get(moves.size() - 1).getCapturedCheckers().addAll(capturedCheckers);
                moves.get(moves.size() - 1).getCapturedCheckers().add(this.board[targetRow - step][targetCol - 1]);
                moves.get(moves.size() - 1).setMovedPiece(piece);

                traverseLeftDiagonal(piece, targetRow, targetCol, side, true, step, moves, moves.get(moves.size() - 1).getCapturedCheckers());
                traverseRightDiagonal(piece, targetRow, targetCol, side, true, step, moves, moves.get(moves.size() - 1).getCapturedCheckers());
            }
            
        }

        return moves;
    }

    public ArrayList<Checker> getAllCheckers(int side)
    {
        ArrayList<Checker> pieces = new ArrayList<Checker>();

        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.cols; j++)
            {
                if (this.board[i][j].getSide() == side)
                    pieces.add(this.board[i][j]);
            }
        }

        return pieces;
    }

    public double evaluatePosition(int row, int col)
    {
        if (row == 0 || row == 7 || col == 0 || col == 7)
        {
            return 5;
        }
        
        return 3;
    }

    // Function for computing the heuristic value of the game state
    // Computer is the maximizer, human is the minimizer
    // Reference: https://github.com/billjeffries/jsCheckersAI/blob/master/scripts/checkers-engine.js
    public double computeHeuristic()
    {
        // Human = red, computer = black
        double computer_pieces = this.blackCheckers;
        double computer_kings = this.blackKings;
        double human_pieces = this.redCheckers;
        double human_kings = this.redKings;
        double computer_pos_sum = 0;
        double human_pos_sum = 0;

        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.cols; j++)
            {
                if (this.board[i][j].getSide() == 2) // human / red
                    human_pos_sum += evaluatePosition(i, j);
                else if (this.board[i][j].getSide() == 1) // computer / black
                    computer_pos_sum += evaluatePosition(i, j);
            }
        }

        double pieceDiff = computer_pieces - human_pieces;
        double kingDiff = computer_kings - human_kings;

        if (human_pieces == 0)
            human_pieces = 0.00001;
        
        double avg_human_pos = human_pos_sum / human_pieces;

        if (computer_pieces == 0) 
            computer_pieces = 0.00001;

        double avg_computer_pos = computer_pos_sum / computer_pieces;
        double avg_pos_diff = avg_computer_pos - avg_human_pos;
    
        double features[] = {pieceDiff, kingDiff, avg_pos_diff};
        double weights[] = {100, 10, 1};
    
        double board_utility = 0;
    
        for (int f = 0; f < features.length; f++)
            board_utility += features[f] * weights[f];
        
        return board_utility;
    }
}
