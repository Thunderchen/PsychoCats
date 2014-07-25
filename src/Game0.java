
import java.util.Scanner;



public class Game0 extends Game{
    
    Scanner s;
    
    public Game0() {
        super();
        s = new Scanner(System.in);
    }

    public void inputAndClose() {
        int i, j;
        while (true) {
            i = -1;
            j = -1;
            while (i < 0 || i > 8) {
                System.out.print("Input i between 0 - 8: ");
                i = s.nextInt();
            }
            while (j < 0 || j > 8) {
                System.out.print("Input j between 0 - 8: ");
                j = s.nextInt();
            }
            if (!tryClose(i, j)) System.out.print("Cannot select that grid!! ");
            else break;
        }
        System.out.println();
        count++;
    }
    
    public void show(int t) {
        show();
    }
    
    public void show() {
        for (int j = 0; j < 9; j++) {
            System.out.print(" " + j + " ");
        }
        System.out.println();
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 1) System.out.print(" ");
            for (int j = 0; j < 9; j++) {
                if (getCatPos() == i * 9 + j) System.out.print("(C)");
                else if (board[i][j] == 0) System.out.print("( )");
                else System.out.print("(*)");
            }
            if (i % 2 == 0) System.out.print(" ");
            System.out.println(" " + i);
        }
        System.out.println("Step: " + count);
        
    }
    
    public void endInfo() {
        if (catEscaped()) System.out.println("Cat escaped!! You lose!!");
        else System.out.println("You get the cat in " + count + " steps!! You win!!");
    }
    
}
