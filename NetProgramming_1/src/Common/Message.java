package Common;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

//网络编程第一次作业理解为定长报文来做
//
public abstract class Message {
    //消息公共基类，定义了消息头的相关信息
     public int totalLength=0;
     public int commandID=-1;
     public  static byte[] getBytes(int data)//用于将int型数据转为byte数组写入文件
     {
         byte[] bytes = new byte[4];
         bytes[0] = (byte) (data & 0xff);
         bytes[1] = (byte) ((data & 0xff00) >> 8);
         bytes[2] = (byte) ((data & 0xff0000) >> 16);
         bytes[3] = (byte) ((data & 0xff000000) >> 24);
         byte tmp=bytes[0];bytes[0]=bytes[3];bytes[3]=tmp;
          tmp=bytes[1];bytes[1]=bytes[2];bytes[2]=tmp;
         return bytes;
     }
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        byte tmp=src[0];src[0]=src[3];src[3]=tmp;
        tmp=src[1];src[1]=src[2];src[2]=tmp;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }
    public static  int readInt(DataInputStream in) throws IOException {
        byte[] tmp = new byte[4];
        in.read(tmp, 0, 4);
        return bytesToInt(tmp, 0);
    }
    public static Message loadIn(DataInputStream input) throws IOException {
        //loadin函数先读入数据类型再更新其他的数据成员
        int  totalLength=readInt(input);
        int commandID=readInt(input);
        if(commandID==1||commandID==3)
        {
            byte[] tmp1=new byte[20];
            byte[] tmp2=new byte[30];
            input.read(tmp1,0,20);
            input.read(tmp2,0,30);
            String username=new String(tmp1);
            String passwd=new String(tmp2);
            if(commandID==1)
                return new registration_request_msg(username,passwd);
            else
                return new login_request_msg(username,passwd);
        }
        else if(commandID==2||commandID==4)
        {
            byte[] tmp1=new byte[1];
            byte[] tmp2=new byte[64];
            input.read(tmp1,0,1);
            input.read(tmp2,0,64);
            String ok=new String(tmp1);
            String description=new String(tmp2);
            if(commandID==2)
                return new registration_response_msg(ok,description);
            else
                return new login_response_msg(ok,description);
        }
        return null;
    }
    public abstract byte[] toByteArray();
}
