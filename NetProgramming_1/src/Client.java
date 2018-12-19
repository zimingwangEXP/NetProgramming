import Common.*;
import java.io.*;
import java.net.Socket;

public class Client {
     public static DataInputStream input=null;
     public static DataOutputStream output=null;
     public static  void main(String[]args) throws IOException {
         login_response_msg bck1=new login_response_msg("","");
         String address="localhost";
         Socket client=new Socket(address,10010);
         input=new DataInputStream(client.getInputStream());
         output=new DataOutputStream(client.getOutputStream());
         //老师的字节数组测试
         byte[] tt=new byte[]{0x00,0x00, 0x00 ,0x3A ,0x00 ,0x00, 0x00, 0x01, 0x74 ,0x6F, 0x6D, 00, 00 ,00
                 ,00, 00, 00 ,00, 00, 00, 00, 00, 00 ,00, 00, 00, 00, 00, 0x31, 0x32, 0x33, 00, 00, 00, 00,
                 00, 00, 00, 00 ,00 ,00, 00, 00, 00 ,00, 00 ,00 ,00 ,00, 00, 00, 00 ,00, 00 ,00 ,00, 00, 00};
         output.write(tt);
         registration_request_msg msg=new registration_request_msg("123","123");
         output.write(msg.toByteArray());
         output.flush();
         registration_response_msg bck2= (registration_response_msg) Message.loadIn(input);
         bck2.Show();
         /*
         login_request_msg mmsg=new login_request_msg("123","123");
         output.write(mmsg.toByteArray());
         output.flush();
         login_response_msg tt= (login_response_msg) Message.loadIn(input);
         tt.Show();
         */
     }
}
