import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

/**
 * Χè��ɨ�׵Ľ�ϰ�
 * ����
 * 1����ɫ�ĸ��ӿ��Ե㣬���֮���Ϊ��ɫ
 * 2��һЩ��ɫ����������èʺ�����èʺ��������
 * 3��è�����߽���ɫ����èʺ�ĸ�����
 * 4����ɫ���ֱ�ʾ��Χ�����������м���������èʺ
 * 5�����ȶ���Ȼ��è��
 * 6����è��·���߾���Ӯ��è�ܵ���Ե������
 * @author Geng
 * ��л��������ʹ��������˹���㷨���ṩ�Ŀμ�StdDraw.java
 *
 */

public class Game2{
    
    final static Font font1 = new Font("΢���ź�", Font.PLAIN, 32);
    final static Font font2 = new Font("΢���ź�", Font.PLAIN, 16);
    
    private Random r;
    protected int[][] board;
    private Cat cat;
    protected int count;
    boolean getShit;

    public Game2() {
        r = new Random();
        board = new int[9][9];
        cat = new Cat();
        count = 0;
        getShit = false;
        StdDraw.setXscale(0, 200);
        StdDraw.setYscale(0, 200);
        initBoard();
    }
    
    private double getX(int i, int j) {
        double offset = (i % 2 == 0) ? 0 : 10;
        return j * 20 + 15 + offset;
    }
    
    private double getY(int i) {
        return 155 - i * 17.5;
    }
    
    public void show(int t) {
        StdDraw.clear();
        StdDraw.setFont(font1);
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (board[i][j] <= 0) StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                else StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.filledCircle(getX(i, j), getY(i), 10);
                if (board[i][j] == 20) drawShit(getX(i, j), getY(i));
                else if (board[i][j] > 0 && board[i][j] < 10) {
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.text(getX(i, j), getY(i), String.valueOf(board[i][j]));
                }
            }
        }
        drawCat(getX(getCatPos() / 9, getCatPos() % 9), getY(getCatPos() / 9));
        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.text(175, 195, "���ã�" + count + " ��");
        StdDraw.show(t);
    }
    
    public void show() {
        show(0);
    }
    
    private int input() {
        boolean isPressing = false;
        while (true) {
            StdDraw.show(40);
            if (StdDraw.mousePressed() && !isPressing) { // ��һ�ΰ������
                isPressing = true;
                continue;
            }
            if (!StdDraw.mousePressed() && isPressing) { // ��һ�ηſ����
                isPressing = false;
                double x = StdDraw.mouseX(), y = StdDraw.mouseY();
                if (y <= 5 || y >= 165 || x <= 5 || x >= 195) continue;
                int i, j;
                if (y >= 155) i = 0;
                else i = (int) ((155 - y) / 17.5);
                if (x <= 15) j = 0;
                else j = (int) ((x - 15) / 20);
                if (inCircle(x, y, i, j)) return i * 9 + j;
                if (i != 8 && inCircle(x, y, i + 1, j)) return (i + 1) * 9 + j;
                if (j != 8 && inCircle(x, y, i, j + 1)) return i * 9 + j + 1;
                if (i != 8 && j != 8 && inCircle(x, y, i + 1, j + 1)) return (i + 1) * 9 + j + 1;
                continue;
            }
        }
    }
    
    public void inputAndClose() {
        int n = input();
        while (!tryClose(n / 9, n % 9)) {
            n = input();
        }
        if (board[n / 9][n % 9] == 20) getShit = true; // �ȵ���èʺ
        count++;
    }
    
    public void endInfo() {
        StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.setFont(font1);
        if (getShit) StdDraw.text(100, 182, "��ȵ���èʺ��");
        else if (catEscaped()) StdDraw.text(100, 182, "�㾹Ȼ��è���ˣ�");
        else StdDraw.text(100, 182, "������ " + count + " ��ץס��è��");
        StdDraw.show();
    }
    
    private boolean inCircle(double x, double y, int i, int j) {
        return (getX(i, j) - x) * (getX(i, j) - x) + (getY(i) - y) * (getY(i) - y) < 100;
    }
    
    /**
     * è�����壨�����Σ���ͷ��Բ�Σ�������������Σ����Ҷ��������Σ�����
     * ��֪��������(x, y)
     * ���������Σ�(x, y + 3)��(x - 2.5, y - 9)��(x + 2.5, y - 9)
     * ͷ��Բ��(x, y + 5)���뾶2.5
     * ��������Σ�(x, y + 5)��(x - 2.5, y + 5)��(x - 2.5, y + 10)
     * �Ҷ������Σ�(x, y + 5)��(x + 2.5, y + 5)��(x + 2.5, y + 10)
     */
    private void drawCat(double x, double y) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledPolygon(new double[]{x,     x - 2.5, x + 2.5}, 
                              new double[]{y + 3, y - 9,   y - 9});
        StdDraw.filledCircle(x, y + 5, 2.5);
        StdDraw.filledPolygon(new double[]{x,     x - 2.5, x - 2.5}, 
                              new double[]{y + 5, y + 5,   y + 10});
        StdDraw.filledPolygon(new double[]{x,     x + 2.5, x + 2.5}, 
                              new double[]{y + 5, y + 5,   y + 10});
        StdDraw.setPenRadius(0.005);
        StdDraw.line(x - 2, y - 8, x - 5, y);
    }
    
    private void drawShit(double x, double y) {
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.filledEllipse(x + 2, y + 2, 1, 6);
        StdDraw.filledEllipse(x, y, 1, 6);
        StdDraw.filledEllipse(x - 2, y - 2, 1, 6);
    }
    
    private void initBoard() {
        int initCloseNum = r.nextInt(5) + r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            tryShit(n / 9, n % 9);
        }
        initCloseNum = r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            if (board[n / 9][n % 9] != -20)
                tryClose(n / 9, n % 9);
        }
    }
    

    public int getCatPos() {
        return cat.getPos();
    }
    
    protected boolean tryClose(int i, int j) {
        if (i * 9 + j == cat.getPos()) return false;
        if (board[i][j] <= 0) {
            if (board[i][j] == 0) board[i][j] = 10;
            else board[i][j] = -board[i][j];
            cat.close(i, j);
            return true;
            }
        return false;
    }
    
    private void tryShit(int i, int j) {
        if (i * 9 + j == cat.getPos()) return;
        if (board[i][j] != -20) {
            board[i][j] = -20;
            ArrayList<Integer> around = cat.around(i * 9 + j);
            for (int w : around) {
                board[w / 9][w % 9]--;
            }
            cat.close(i, j);
        }
    }
    
    protected boolean catTryMove() {
        return cat.tryMove();
    }
    
    public boolean catEscaped() {
        return cat.escaped();
    }
    
    private void run() {
        show();
        while (!catEscaped()) {
            inputAndClose();
            show();
            if (getShit) break;
            show(1000);
            if (!catTryMove()) break;
            show();
        }
        endInfo();
    }
    
    public static void main(String[] args) {
        Game2 g = new Game2();
        g.run();
    }
    
}
