import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[]args) throws IOException, InterruptedException {
        Socket client=new Socket("127.0.0.1",12345);
        DataInputStream in=new DataInputStream(client.getInputStream());
        while(true)
        {
            //Thread.sleep(5000);
            System.out.println(in.readInt());
        }
    }
}