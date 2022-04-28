import java.util.ArrayList;

public class Move {
    
    private int row;
    private int col;
    private ArrayList<Checker> capturedCheckers;
    private Checker movedPiece;

    public Move(int row, int col)
    {
        this.row = row;
        this.col = col;
        this.capturedCheckers = new ArrayList<Checker>();
    }

    public Move(int row, int col, ArrayList<Checker> capturedCheckers)
    {
        this.row = row;
        this.col = col;
        this.capturedCheckers = new ArrayList<Checker>();
        this.capturedCheckers.addAll(capturedCheckers);
    }

    public int getRow()
    {
        return this.row;
    }

    public int getCol()
    {
        return this.col;
    }

    public ArrayList<Checker> getCapturedCheckers()
    {
        return this.capturedCheckers;
    }

    public void setMovedPiece(Checker piece)
    {
        this.movedPiece = piece;
    }

    public boolean equals(int row, int col)
    {
        if (this.row == row && this.col == col)
            return true;

        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null)
        {
            Move other = (Move) obj;
            
            return other.row == this.row && other.col == this.col;
        }
        return false;
    }
}
