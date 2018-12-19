package Common;

import javax.activation.CommandMap;
import java.io.DataInputStream;
import java.io.IOException;

public class request_msg extends Message {
    //请求消息基类定义
    public  String userName=null;
    public  String passwd=null;
    public request_msg(String userName,String passwd){
        while(userName.length()<20)
        {
            userName=userName+'\0';
        }
        while(passwd.length()<30)
        {
            passwd=passwd+'\0';
        }
        this.userName=userName;
        this.passwd=passwd;
        totalLength=8+userName.length()+passwd.length();
    }
    @Override
    public byte[] toByteArray() {
        assert (commandID<=4&&commandID>=1):"commonID无效，请是否确认初始消息类型!!!\n";
        byte[] ret = new byte[totalLength];
       // System.out.println(Message.getBytes(totalLength));
        System.arraycopy(Message.getBytes(totalLength),0,ret,0,4);
        System.arraycopy(Message.getBytes(commandID),0,ret,4,4);
       // System.out.println(Message.getBytes(commandID));
        System.arraycopy(userName.getBytes(),0,ret,8, userName.length());
        System.arraycopy(passwd.getBytes(),0,ret,8+userName.length(),passwd.length());
        return ret;
    }
    public void Show(){
        System.out.println("commandID:"+commandID);
        System.out.println("totalLength:"+totalLength);
        System.out.println("userName:"+userName);
        System.out.println("passwd:"+passwd);
    }
}
