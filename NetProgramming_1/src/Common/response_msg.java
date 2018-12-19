package Common;

import java.io.DataInputStream;
import java.io.IOException;

public class response_msg extends Message{
    //响应消息基类定义
    public  String status=null;
    public String  description=null;
    public response_msg(String status,String description){
        while(description.length()<64) {
            description = description+'\0';
        }
        this.status=status;
        this.description=description;
        totalLength=8+status.length()+description.length();
    }
    @Override
    public byte[] toByteArray() {
        assert (commandID<=4&&commandID>=1):"commonID无效，请是否确认初始消息类型!!!\n";
        byte[] ret = new byte[totalLength];
        System.arraycopy(Message.getBytes(totalLength),0,ret,0,4);
        System.arraycopy(Message.getBytes(commandID),0,ret,4,4);
        System.arraycopy(status.getBytes(),0,ret,8, status.length());
        System.arraycopy(description.getBytes(),0,ret,8+status.length(),description.length());
        return ret;
    }
    public void Show(){
        System.out.println("commandID:"+commandID);
        System.out.println("totalLength:"+totalLength);
        System.out.println("status:"+status);
        System.out.println("description:"+description);
    }
}
