import java.util.Scanner;

public class Main 
{
    public static final int ROWS = 8;
    public static final int COLS = 8;
    public static void main(String[] args) 
    {
        Scanner scan = new Scanner(System.in);

        GameMaster game = new GameMaster(scan, ROWS, COLS);

        game.playGame();
    
        scan.close();
    }
} 