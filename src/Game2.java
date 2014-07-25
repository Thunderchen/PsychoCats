import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

/**
 * 围猫和扫雷的结合版
 * 规则：
 * 1，灰色的格子可以点，点击之后变为黄色
 * 2，一些灰色格子下面有猫屎，点击猫屎格子算输
 * 3，猫不会走进黄色和有猫屎的格子里
 * 4，红色数字表示周围六个格子中有几个格子有猫屎
 * 5，你先动，然后猫动
 * 6，让猫无路可走就算赢，猫跑到边缘就算输
 * @author Geng
 * 致谢：本程序使用了普林斯顿算法可提供的课件StdDraw.java
 *
 */

public class Game2{
    
    final static Font font1 = new Font("微软雅黑", Font.PLAIN, 32);
    final static Font font2 = new Font("微软雅黑", Font.PLAIN, 16);
    
    private Random r;
    // board[i][j]的数值有如下几种状态：
    // -20：此格有猫屎，且未翻开
    // -6 至 -1：此格临近格子有猫屎，数量为数字绝对值，且未翻开
    // 0：此格无任何异常，且未翻开
    // 1 至  6：此格临近格子有猫屎，且已翻开
    // 10：此格无任何异常，且已翻开
    // 20：此格有猫屎，且已翻开
    protected int[][] board;
    private Cat cat;
    protected int count; // 记录步数
    boolean getShit; // 判断是否已踩到猫屎

    public Game2() {
        r = new Random();
        board = new int[9][9];
        cat = new Cat(); // 猫的初始位置在board[4][4]
        count = 0;
        getShit = false;
        StdDraw.setXscale(0, 200);
        StdDraw.setYscale(0, 200);
        initBoard();
    }
    
    // 以下两个方法将格子的序号（两个整数i, j）换算为其圆心的坐标
    private double getX(int i, int j) {
        double offset = (i % 2 == 0) ? 0 : 10;
        return j * 20 + 15 + offset;
    }
    
    private double getY(int i) {
        return 155 - i * 17.5;
    }
    
    // 描绘当前场景
    public void show(int t) {
        StdDraw.clear();
        StdDraw.setFont(font1);
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                if (board[i][j] <= 0) StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                else StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.filledCircle(getX(i, j), getY(i), 10); // 画格子
                if (board[i][j] == 20) drawShit(getX(i, j), getY(i)); // 画猫屎
                else if (board[i][j] > 0 && board[i][j] < 10) { // 填数字
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.text(getX(i, j), getY(i) - 2, String.valueOf(board[i][j]));
                }
            }
        }
        drawCat(getX(cat.getPos() / 9, cat.getPos() % 9), getY(cat.getPos() / 9)); // 画猫
        StdDraw.setFont(font2);
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.text(175, 195, "已用：" + count + " 步"); // 右上角步数统计
        StdDraw.show(t); // 延时
    }
    
    public void show() {
        show(0);
    }
    
    // 记录鼠标松开时的位置，计算其位于哪一个格子内，返回其序号（一个整数n）
    // 若鼠标并未位于任何格子内，重复直到输入有效
    private int input() {
        boolean isPressing = false;
        while (true) {
            StdDraw.show(40); // 每40毫秒扫描一次鼠标状况
            if (StdDraw.mousePressed() && !isPressing) { // 第一次按下鼠标
                isPressing = true;
                continue;
            }
            if (!StdDraw.mousePressed() && isPressing) { // 第一次放开鼠标
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
    
    // 若坐标点(x, y)位于序号为[i, j]的各自内部，返回true
    private boolean inCircle(double x, double y, int i, int j) {
        return (getX(i, j) - x) * (getX(i, j) - x) + (getY(i) - y) * (getY(i) - y) < 100;
    }
    
    // 接受鼠标位置信息，判断此格是否可以翻开
    public void inputAndClose() {
        int n = input();
        while (!tryClose(n / 9, n % 9)) { // 若此格已翻开或被猫占据，tryClose返回false
            n = input();
        }
        if (board[n / 9][n % 9] == 20) getShit = true; // 踩到了猫屎
        count++;
    }
    
    // 游戏结束时的显示信息
    public void endInfo() {
        StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.setFont(font1);
        if (getShit) StdDraw.text(100, 182, "你踩到了猫屎！");
        else if (cat.escaped()) StdDraw.text(100, 182, "你竟然让猫跑了！");
        else StdDraw.text(100, 182, "你用了 " + count + " 步抓住了猫！");
        StdDraw.show();
    }
    

    /**
     * 猫由身体（三角形）、头（圆形）、左耳（三角形）、右耳（三角形）构成
     * 已知中心坐标(x, y)
     * 身体三角形：(x, y + 3)、(x - 2.5, y - 9)、(x + 2.5, y - 9)
     * 头：圆心(x, y + 5)，半径2.5
     * 左耳三角形：(x, y + 5)、(x - 2.5, y + 5)、(x - 2.5, y + 10)
     * 右耳三角形：(x, y + 5)、(x + 2.5, y + 5)、(x + 2.5, y + 10)
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
    
    // 猫屎由并排的三个椭圆形构成
    private void drawShit(double x, double y) {
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.filledEllipse(x + 2, y + 2, 1, 6);
        StdDraw.filledEllipse(x, y, 1, 6);
        StdDraw.filledEllipse(x - 2, y - 2, 1, 6);
    }
    
    // 游戏初始化
    private void initBoard() {
        // 产生猫屎格子数量
        int initCloseNum = r.nextInt(5) + r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            tryShit(n / 9, n % 9); // 埋下猫屎
        }
        // 产生初始翻开格子数量
        initCloseNum = r.nextInt(5) + 3;
        for (; initCloseNum > 0; initCloseNum--) {
            int n = r.nextInt(81);
            if (board[n / 9][n % 9] != -20) // 埋有猫屎的格子不翻开
                tryClose(n / 9, n % 9);
        }
    }
    
// 这个方法已经废弃不用
//    public int getCatPos() {
//        return cat.getPos();
//    }
    
    // 尝试翻开一个格子，若成功，返回true
    // tryClose的意思是：未翻开的格子是向猫“开放”的，翻开的格子则对猫“关闭”
    private boolean tryClose(int i, int j) {
        if (i * 9 + j == cat.getPos()) return false; // 猫占据的格子不能翻开
        if (board[i][j] <= 0) { // 所有未翻开的格子均有board[i][j] <= 0
            if (board[i][j] == 0) board[i][j] = 10; // 普通格子
            else board[i][j] = -board[i][j]; // 埋有猫屎或邻近埋有猫屎的格子
            cat.close(i, j); // 通知猫：这个格子已向其关闭
            return true;
            }
        return false; // 已翻开的格子会执行此条语句
    }
    
    // 尝试在一个格子中埋下猫屎。若格子已经埋有猫屎，则不作处理
    private void tryShit(int i, int j) {
        if (i * 9 + j == cat.getPos()) return;
        if (board[i][j] != -20) {
            board[i][j] = -20;
            // 通过猫类的方法得到一个格子周围的格子（不含已经埋有猫屎的）
            ArrayList<Integer> around = cat.around(i * 9 + j);
            for (int w : around) {
                board[w / 9][w % 9]--; // 更新其数字记录
            }
            cat.close(i, j); // 通知猫：这个格子已向其关闭
        }
    }
    
// 这个方法已废弃不用
//    protected boolean catTryMove() {
//        return cat.tryMove();
//    }
    
// 这个方法已废弃不用 
//    public boolean catEscaped() {
//        return cat.escaped();
//    }
    
    // 主程序代码
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
