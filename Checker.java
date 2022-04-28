public class Checker 
{
    private int row;
    private int col;
    private int side;
    private boolean isKing; 
    
    public Checker(int row, int col, int side)
    {
        this.row = row;
        this.col = col;
        this.side = side;       // side 0 = null, 1 = black, 2 = red
        this.isKing = false;
    }    

    public int getRow()
    {
        return this.row;
    }

    public int getCol()
    {
        return this.col;
    }

    public int getSide()
    {
        return this.side;
    }

    public boolean checkIfKing()
    {
        return this.isKing;
    }

    public void makeKing()
    {
        this.isKing = true;
    }

    public void setSide(int side)
    {
        this.side = side;
    }

    public void move(int targetRow, int targetCol)
    {
        this.row = targetRow;
        this.col = targetCol;
    }

    @Override
    public String toString()
    {
        String piece = " ";

        if (isKing)
        {
            switch (side)
            {
                case 1:
                    piece = "B";
                    break;

                case 2: 
                    piece = "R";
                    break;

                default:
                    break;
            }
        }
        else
        {
            switch (side)
            {
                case 1:
                    piece = "b";
                    break;

                case 2: 
                    piece = "r";
                    break;

                default:
                    break;
            }
        }

        return piece;
    }
}
