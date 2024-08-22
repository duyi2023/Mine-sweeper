/**
 * Created with IntelliJ IDEA.
 * @Author: 杜仪、杨昕玉
 * @Date: 2024/6/15
 * @Description:输入输出功能
 */

import java.io.*;
public class Save {
    //保存数据
    public void write(int[] a) throws IOException {
        FileOutputStream fos = new FileOutputStream("Garde.dat");
        DataOutputStream dos = new DataOutputStream(fos);
        for(int i = 0;i<4;i++) {
            System.out.println(a[i]+"  ");
            dos.writeInt(a[i]);
        }
        fos.close();
        dos.close();
    }
    //读出数据
    public int[] read() throws IOException {
        int [] a = new int[4];
        FileInputStream fis = new FileInputStream("Garde.dat");
        DataInputStream dis = new DataInputStream(fis);
        for(int i = 0;i<4;i++)
            a[i] = dis.readInt();
        fis.close();
        dis.close();
        return a;
    }
    //清空历史数据
    public void clear() throws IOException {
        FileOutputStream fos = new FileOutputStream("Garde.dat");
        DataOutputStream dos = new DataOutputStream(fos);
        for(int i = 0;i<4;i++) dos.writeInt(0);
        fos.close();
        dos.close();
    }
}
