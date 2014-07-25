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
    // board[i][j]����ֵ�����¼���״̬��
    // -20���˸���èʺ����δ����
    // -6 �� -1���˸��ٽ�������èʺ������Ϊ���־���ֵ����δ����
    // 0���˸����κ��쳣����δ����
    // 1 ��  6���˸��ٽ�������èʺ�����ѷ���
    // 10���˸����κ��쳣�����ѷ���
    // 20���˸���èʺ�����ѷ���
    protected int[][] board;
    private Cat cat;
    protected int count; // ��¼����
    boolean getShit; // �ж��Ƿ��Ѳȵ�èʺ

    public Game2() {
        r = new Random();
        board = new int[9][9];
        cat = new Cat(); // è�ĳ�ʼλ����board[4][4]
        count = 0;
        getShit = false;
        StdDraw.setXscale(0, 200);
        StdDraw.setYscale(0, 200);
        initBoard();
    }
    
    // �����������������ӵ���ţ���������i, j������Ϊ��Բ�ĵ�����
    private double getX(int i, int j) {
        double offset = (i % 2 == 0) ? 0 : 10;
        return j * 20 + 15 + offset;
    }
    
    private double getY(int i) {
        return 155 - i * 17.5;
    }
    
    // ��浱ǰ����
    public void show(int t) {
        StdDraw.clear();
        StdDraw.setFont(font1);
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (board[i][j] <= 0) StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                else StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.filledCircle(getX(i, j), getY(i), 10); // ������
                if (board[i][j] == 20) drawShit(getX(i, j), getY(i)); // ��èʺ
                else if (board[i][j] > 0 && board[i][j] < 10) { // ������
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.text(getX(i, j), getY(i) - 2, String.valueOf(board[i][j]));
                }
            }
        }
        drawCat(getX(cat.getPos() / 9, cat.getPos() % 9), getY(cat.getPos() / 9)); // ��è
        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.text(175, 195, "���ã�" + count + " ��"); // ���Ͻǲ���ͳ��
        StdDraw.show(t); // ��ʱ
    }
    
    public void show() {
        show(0);
    }
    
    // ��¼����ɿ�ʱ��λ�ã�������λ����һ�������ڣ���������ţ�һ������n��
    // ����겢δλ���κθ����ڣ��ظ�ֱ��������Ч
    private int input() {
        boolean isPressing = false;
        while (true) {
            StdDraw.show(40); // ÿ40����ɨ��һ�����״��
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
    
    // �������(x, y)λ�����Ϊ[i, j]�ĸ����ڲ�������true
    private boolean inCircle(double x, double y, int i, int j) {
        return (getX(i, j) - x) * (getX(i, j) - x) + (getY(i) - y) * (getY(i) - y) < 100;
    }
    
    // �������λ����Ϣ���жϴ˸��Ƿ���Է���
    public void inputAndClose() {
        int n = input();
        while (!tryClose(n / 9, n % 9)) { // ���˸��ѷ�����èռ�ݣ�tryClose����false
            n = input();
        }
        if (board[n / 9][n % 9] == 20) getShit = true; // �ȵ���èʺ
        count++;
    }
    
    // ��Ϸ����ʱ����ʾ��Ϣ
    public void endInfo() {
        StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.setFont(font1);
        if (getShit) StdDraw.text(100, 182, "��ȵ���èʺ��");
        else if (cat.escaped()) StdDraw.text(100, 182, "�㾹Ȼ��è���ˣ�");
        else StdDraw.text(100, 182, "������ " + count + " ��ץס��è��");
        StdDraw.show();
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
    
    // èʺ�ɲ��ŵ�������Բ�ι���
    private void drawShit(double x, double y) {
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.filledEllipse(x + 2, y + 2, 1, 6);
        StdDraw.filledEllipse(x, y, 1, 6);
        StdDraw.filledEllipse(x - 2, y - 2, 1, 6);
    }
    
    // ��Ϸ��ʼ��
    private void initBoard() {
        // ����èʺ��������
        int initCloseNum = r.nextInt(5) + r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            tryShit(n / 9, n % 9); // ����èʺ
        }
        // ������ʼ������������
        initCloseNum = r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            if (board[n / 9][n % 9] != -20) // ����èʺ�ĸ��Ӳ�����
                tryClose(n / 9, n % 9);
        }
    }
    
// ��������Ѿ���������
//    public int getCatPos() {
//        return cat.getPos();
//    }
    
    // ���Է���һ�����ӣ����ɹ�������true
    // tryClose����˼�ǣ�δ�����ĸ�������è�����š��ģ������ĸ������è���رա�
    private boolean tryClose(int i, int j) {
        if (i * 9 + j == cat.getPos()) return false; // èռ�ݵĸ��Ӳ��ܷ���
        if (board[i][j] <= 0) { // ����δ�����ĸ��Ӿ���board[i][j] <= 0
            if (board[i][j] == 0) board[i][j] = 10; // ��ͨ����
            else board[i][j] = -board[i][j]; // ����èʺ���ڽ�����èʺ�ĸ���
            cat.close(i, j); // ֪ͨè���������������ر�
            return true;
            }
        return false; // �ѷ����ĸ��ӻ�ִ�д������
    }
    
    // ������һ������������èʺ���������Ѿ�����èʺ����������
    private void tryShit(int i, int j) {
        if (i * 9 + j == cat.getPos()) return;
        if (board[i][j] != -20) {
            board[i][j] = -20;
            // ͨ��è��ķ����õ�һ��������Χ�ĸ��ӣ������Ѿ�����èʺ�ģ�
            ArrayList<Integer> around = cat.around(i * 9 + j);
            for (int w : around) {
                board[w / 9][w % 9]--; // ���������ּ�¼
            }
            cat.close(i, j); // ֪ͨè���������������ر�
        }
    }
    
// ��������ѷ�������
//    protected boolean catTryMove() {
//        return cat.tryMove();
//    }
    
// ��������ѷ������� 
//    public boolean catEscaped() {
//        return cat.escaped();
//    }
    
    // ���������
    private void run() {
        show();
        while (!cat.escaped()) {
            inputAndClose();
            show();
            if (getShit) break;
            show(1000);
            if (!cat.tryMove()) break;
            show();
        }
        endInfo();
    }
    
    public static void main(String[] args) {
        Game2 g = new Game2();
        g.run();
    }
    
}
