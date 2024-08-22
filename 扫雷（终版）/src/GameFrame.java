/**
 * Created with IntelliJ IDEA.
 * @Author: 杜仪、杨昕玉
 * @Date: 2024/6/15
 * @Description:扫雷游戏界面
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Random;

public class GameFrame extends JFrame {
    // 网格像素大小
    final int grid_h = 38;
    final int grid_l = 40;

    //棋盘数据
    int mode, n, m;//游戏模式，棋盘尺寸
    int mine_num, flag_num;//棋盘中存在的地雷个数，和旗帜个数
    int[][] Mine = new int[22][32];//地雷分布    地雷状态表示，-1表示为有地雷，0~8表示周围地雷个数
    int[][] Masks = new int[22][32];//棋盘中层   蒙版层状态表示，1表示存在，0表示不存在
    int[][] Mark = new int[22][32];//棋盘上层    标记层，0表示没有标记，1表示为旗帜，2表示为问号
    int[] grade;//历史最优成绩

    //最优成绩保存模块
    Save save = new Save();

    // 菜单组件
    JMenuBar jmenubar = new JMenuBar();
    JMenu yx = new JMenu("游戏");
    JMenuItem regame = new JMenuItem("重新开始");
    JMenuItem exit = new JMenuItem("退出游戏");

    // 窗口组件
    Font font = new Font("黑体", Font.BOLD, 30);// 统一字体格式
    ImageIcon Mines_icon = new ImageIcon("素材\\图标\\地雷.png");// 地雷数量显示组件
    JLabel Mine_img = new JLabel(Mines_icon);
    JLabel Mine_nums = new JLabel("0");
    ImageIcon sandglass = new ImageIcon("素材\\图标\\沙漏.png");// 计时组件
    JLabel sandglass_img = new JLabel(sandglass);
    JLabel time_text = new JLabel("0");

    // 棋盘区域
    JButton[][] Mjb;
    JPanel Chess;

    //计时功能
    int time_num = 0;
    Timer clock = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            time_num++;
            time_text.setText(time_num + "");
        }
    });

    //窗口搭建，和游戏数据初始化
    GameFrame(int Mode,int h,int l,int num) {
        //历史最优成绩读入
        try {
            grade = save.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //游戏数据初始化
        Chaessinit(Mode,h,l,num);

        //菜单事件添加
        regame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clock.stop();
                setVisible(false);
                new GameFrame(mode,n,m,mine_num);
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clock.stop();
                setVisible(false);
                new MenuFrame();
            }
        });

        // 添加菜单
        regame.setFont(new Font("黑体", Font.BOLD, 12));
        exit.setFont(new Font("黑体", Font.BOLD, 12));
        yx.add(regame);
        yx.add(exit);
        yx.setFont(new Font("黑体", Font.BOLD, 12));
        jmenubar.add(yx);
        setJMenuBar(jmenubar);

        // 添加地雷数量和计时器显示
        JPanel topPanel = new JPanel(new GridLayout(1, 4));
        topPanel.add(Mine_img);
        Mine_nums.setFont(font);
        Mine_nums.setText(mine_num + "");
        topPanel.add(Mine_nums);
        topPanel.add(sandglass_img);
        time_text.setFont(font);
        topPanel.add(time_text);
        topPanel.setSize(grid_h * m, 40);

        // 添加棋盘区域
        Mjb = new JButton[n + 1][m + 1];
        Chess = new JPanel(new GridLayout(n, m));
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++) {
                JButton jb = new JButton();
                jb.setSize(grid_h, grid_l);
                jb.setBackground(Color.black);
                jb.addMouseListener(new ButtonListener(i, j));
                jb.setIcon(new ImageIcon("素材\\棋盘\\1.png"));
                Mjb[i][j] = jb;
                Chess.add(jb);
            }

        add(topPanel, BorderLayout.NORTH);
        add(Chess, BorderLayout.CENTER);

        //窗口属性设置
        setTitle("扫雷");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(grid_h * m, grid_l * n + 40);
        setLocationRelativeTo(null);//窗体居中显示
        setResizable(false);
        setVisible(true);

        //计时器线程开启
        clock.start();
    }

    //棋盘创建
    public void Chaessinit(int Mode,int h,int l,int num) {
        this.mode = Mode;
        //根据难度创建不同大小的棋盘
        switch (Mode) {
            case 1: {
                n = m = 9;
                mine_num = 10;
                Create();
            }
            break;
            case 2: {
                n = m = 16;
                mine_num = 40;
                Create();
            }
            break;
            case 3: {
                n = 20;
                m = 30;
                mine_num = 99;
                Create();
            }
            break;
            case 4: {
                //自定义难度
                n = h;
                m = l;
                mine_num = num;
                Create();
            }
            break;
        }
        developer();
    }

    //根据模式创建棋盘
    public void Create() {
        Random rand = new Random();
        for (int i = 0; i < mine_num; i++) {//地雷布置
            while (true) {
                int x = rand.nextInt(n) + 1;
                int y = rand.nextInt(m) + 1;
                if (Mine[x][y] == 0) {
                    Mine[x][y] = -1;
                    break;
                }
            }
        }
        for (int i = 1; i <= n; i++)//周围地雷个数统计
            for (int j = 1; j <= m; j++)
                if (Mine[i][j] != -1) Mine[i][j] = countMine(i, j);

        for (int i = 1; i <= n; i++)//蒙版状态初始化为1
            for (int j = 1; j <= m; j++)
                Masks[i][j] = 1;
    }

    //统计周围格子地雷的数量
    public int countMine(int x, int y) {
        int cnt = 0;
        for (int i = x - 1; i <= x + 1; i++)
            for (int j = y - 1; j <= y + 1; j++)
                if (Mine[i][j] == -1) cnt++;
        return cnt;
    }

    //扫雷
    public void Sweeping(int x, int y) {
        if (Mark[x][y] == 1 || Mark[x][y] == 2) return;
        Masks[x][y] = 0;//掀开蒙版
        if (Mine[x][y] == -1) {//是地雷，执行失败的相关逻辑
            clock.stop();
            for (int i = 1; i <= n; i++)//翻开整个棋盘
                for (int j = 1; j <= m; j++)
                    Mjb[i][j].setIcon(new ImageIcon("素材\\数字\\" + Mine[i][j] + ".png"));
            JOptionPane.showMessageDialog(null, "闯关失败", "", JOptionPane.PLAIN_MESSAGE);
            setVisible(false);
            new MenuFrame();
        } else if (Mine[x][y] != 0) {//周围存在地雷，在当前位置显示周围地雷数量
            Mjb[x][y].setIcon(new ImageIcon("素材\\数字\\" + Mine[x][y] + ".png"));
        } else if (Mine[x][y] == 0) {//当前位置为零
            dfs(x, y);
            for (int i = 1; i <= n; i++)
                for (int j = 1; j <= m; j++)
                    if (Mine[i][j] == -2) Mine[i][j] = 0;
        }
    }

    //搜索空白区域并显示出来
    public void dfs(int x, int y) {
        if (x < 1 || y < 1 || x > n || y > m || Mine[x][y] == -2 || Mark[x][y] == 1 || Mark[x][y] == 2) return;//防止越界
        Masks[x][y] = 0;
        Mjb[x][y].setIcon(new ImageIcon("素材\\数字\\" + Mine[x][y] + ".png"));
        if (Mine[x][y] == 0) {//如果是数字表示到达边界，停止搜索
            Mjb[x][y].setIcon(new ImageIcon("素材\\数字\\" + Mine[x][y] + ".png"));
            Mine[x][y] = -2;
            dfs(x - 1, y);
            dfs(x, y - 1);
            dfs(x + 1, y);
            dfs(x, y + 1);
        }
    }

    //打标记，三个状态，0无、1插旗、2问号
    public void setMark(int x, int y) {
        Mark[x][y] = (Mark[x][y] + 1) % 3;
        if (Mark[x][y] == 1) flag_num++;
        if (Mark[x][y] == 2) flag_num--;
    }

    //判断是否胜利，遍历棋盘查看非地雷区域是否都被翻开
    public boolean isWin() {
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++) {
                if (Mine[i][j] == -1) continue;
                if (Masks[i][j] == 1) return false;
            }
        return true;
    }

    //自定义鼠标监听，存储按钮位置坐标，方便游戏逻辑实现
    class ButtonListener extends MouseAdapter {
        int x;
        int y;

        ButtonListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int flag = e.getButton();//1鼠标左键，3鼠标右键
            if (Masks[x][y] == 0) return;//如果被翻开过则不触发事件响应
            if (flag == 1) {//左击
                Sweeping(x, y);
                if (isWin()) {//闯关胜利交互部分
                    clock.stop();
                    if (grade[mode - 1] == 0 || grade[mode - 1] > time_num) grade[mode - 1] = time_num;//更新最优成绩
                    try {
                        save.write(grade);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    for (int i = 1; i <= n; i++)//翻开整个棋盘
                        for (int j = 1; j <= m; j++)
                            Mjb[i][j].setIcon(new ImageIcon("素材\\数字\\" + Mine[i][j] + ".png"));
                    JOptionPane.showMessageDialog(null, "恭喜你闯关成功！\n用时：" + time_num + "s", "", JOptionPane.PLAIN_MESSAGE);
                    setVisible(false);
                    new MenuFrame();
                }
                developer();
            } else if (flag == 3) {//右击
                setMark(x, y);
                Mjb[x][y].setIcon(new ImageIcon("素材\\旗帜\\" + Mark[x][y] + ".png"));
                Mine_nums.setText((mine_num - flag_num) + "");
            }
        }
    }

    //开发者方法，方便查看游戏内部运行情况
    public void developer() {
        print(Mine);
        print(Masks);
        print(Mark);
    }

    //打印数组到控制台
    public void print(int[][] a) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++)
                System.out.printf("%-4d", a[i][j]);
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}
