/**
 * Created with IntelliJ IDEA.
 * @Author: 杜仪、杨昕玉
 * @Date: 2024/6/15
 * @Description:扫雷菜单界面
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuFrame extends JFrame implements ActionListener {
    //背景图片
    ImageIcon bjimg = new ImageIcon("素材\\图标\\背景图.png");
    JLabel bj = new JLabel(bjimg);

    //功能按钮
    JButton Mode1 = new JButton(new ImageIcon("素材\\图标\\初级难度.png"));
    JButton Mode2 = new JButton(new ImageIcon("素材\\图标\\中等难度.png"));
    JButton Mode3 = new JButton(new ImageIcon("素材\\图标\\高级难度.png"));
    JButton Mode4 = new JButton(new ImageIcon("素材\\图标\\自定义.png"));

    //最佳成绩组件
    Font font = new Font("黑体", Font.BOLD, 20);
    JLabel topjc = new JLabel("最佳成绩");
    JLabel jc1 = new JLabel();
    JLabel jc2 = new JLabel();
    JLabel jc3 = new JLabel();
    JLabel jc4 = new JLabel();
    Save save = new Save();
    int[] a;


    MenuFrame() {
        //组件组装
        setLayout(null);
        Mode1.setBounds(150, 140, 200, 50);
        Mode1.addActionListener(this);
        add(Mode1);
        Mode2.setBounds(150, 205, 200, 50);
        Mode2.addActionListener(this);
        add(Mode2);
        Mode3.setBounds(150, 270, 200, 50);
        Mode3.addActionListener(this);
        add(Mode3);
        Mode4.setBounds(150, 335, 200, 50);
        Mode4.addActionListener(this);
        add(Mode4);

        try {
            a = save.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        topjc.setBounds(380, 90, 120, 50);
        topjc.setFont(new Font("楷体", Font.BOLD, 25));
        topjc.setForeground(new Color(0,2,122));

        jc1.setBounds(400, 145, 120, 40);
        if (a[0] != 0)
            jc1.setText(a[0] + "s");
        else
            jc1.setText("无记录");
        jc1.setFont(new Font("楷体", Font.BOLD, 20));
        jc1.setForeground(new Color(0,2,122));

        jc2.setBounds(400, 210, 120, 40);
        if (a[1] != 0)
            jc2.setText(a[1] + "s");
        else
            jc2.setText("无记录");
        jc2.setFont(new Font("楷体", Font.BOLD, 20));
        jc2.setForeground(new Color(0,2,122));

        jc3.setBounds(400, 275, 120, 40);
        if (a[2] != 0)
            jc3.setText(a[2] + "s");
        else
            jc3.setText("无记录");
        jc3.setFont(new Font("楷体", Font.BOLD, 20));
        jc3.setForeground(new Color(0,2,122));

        jc4.setBounds(400, 340, 120, 40);
        if (a[3] != 0)
            jc4.setText(a[3] + "s");
        else
            jc4.setText("无记录");
        jc4.setFont(new Font("楷体", Font.BOLD, 25));
        jc4.setForeground(new Color(0,2,122));

        add(topjc);
        add(jc1);
        add(jc2);
        add(jc3);
        add(jc4);

        //背景设置
        bj.setBounds(0, 0, 500, 500);
        add(bj);

        //窗口属性设置
        setTitle("扫雷");
        setDefaultCloseOperation(3);
        setResizable(false);
        setSize(500, 500);
        setLocationRelativeTo(null);//窗体居中显示
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int mode = 0;
        if (e.getSource() == Mode1) {
            setVisible(false);
            new GameFrame(1,0,0,0);
        }
        if (e.getSource() == Mode2) {
            setVisible(false);
            new GameFrame(2,0,0,0);
        }
        if (e.getSource() == Mode3) {
            setVisible(false);
            new GameFrame(3,0,0,0);
        }
        if (e.getSource() == Mode4) {
            int num1 = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入行(最多20行）", "难度选择", JOptionPane.QUESTION_MESSAGE));
            int num2 = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入列（最多30列）", "难度选择", JOptionPane.QUESTION_MESSAGE));
            int num3 = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入地雷的数量（最多99个）", "难度选择", JOptionPane.QUESTION_MESSAGE));
            if (num1 < 0 || num1 > 20 || num2 < 0 || num2 > 30 || num3 < 1 || num3 > 99 || num3 >= num1 * num2) {
                JOptionPane.showMessageDialog(null, "输入不合法", "", JOptionPane.PLAIN_MESSAGE);
            }else {
                setVisible(false);
                new GameFrame(4,num1,num2,num3);
            }
        }
    }

    public static void main(String[] args) {
        new MenuFrame();
    }
}
