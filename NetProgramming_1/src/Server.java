import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Common.*;
public class Server {
    public static String file="user_data.data";
    public static  ObjectOutputStream file_output=null;
    public static  ObjectInputStream file_input=null;
    private static  int port,backlog;
    private static  ServerSocket server=null;
    private static ConcurrentHashMap<String,String> userlist=new ConcurrentHashMap<>();
    public static Socket connection=null;
    public static ExecutorService executor=null;
    static class Timer implements  Runnable {
        public Timer(String path) throws IOException {
              if(path!=null)
              {
                  file=path;
              }
              file_output=new ObjectOutputStream(new FileOutputStream(file));
        }
        @Override
        public void run() {
            while(true)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    file_output.writeObject(userlist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
          }
       }
    //事件处理线程
   static class workthread implements  Runnable{
        public Socket connection=null;
        private static  DataInputStream input=null;
        private static  DataOutputStream output=null;
        public workthread(Socket thread) throws IOException {
            this.connection=thread;
            input=new DataInputStream(thread.getInputStream());
            output=new DataOutputStream(thread.getOutputStream());
        }
        void loginHandle(login_request_msg request) throws IOException {
            login_response_msg back=null;
            request.Show();
            if(userlist.containsKey(request.userName))
            {
                if(info.EncoderByMd5(request.passwd).equals(userlist.get(request.userName)))
                {
                    back=new login_response_msg("1","login successful:"+request.userName);
                    output.write(back.toByteArray());
                    output.flush();
                }
                else
                {
                    back=new login_response_msg("0","login failed：it's not matched for the username and the password");
                    output.write(back.toByteArray());
                    output.flush();
                }
            }
            else
            {
                back=new login_response_msg("0","login failed:there isn't this username in database");
                output.write(back.toByteArray());
                output.flush();
            }
        }
        void RegistHandle( registration_request_msg request) throws IOException {
            registration_response_msg back=null;
            request.Show();
            if(userlist.contains(request.userName))
            {
               back=new registration_response_msg("0","regist failed:the username is repeated");
               output.write(back.toByteArray());
               output.flush();
            }
            else
            {
                userlist.put(request.userName,info.EncoderByMd5(request.passwd));
                back=new registration_response_msg("1","regist successful");
                output.write(back.toByteArray());
                output.flush();
            }
        }
        @Override
        public void run() {
            int cnt=0;
            while (true) {
                if(cnt>3)break;
                try {
                    long startTime=System.currentTimeMillis();
                    Message id = Message.loadIn(input);
                    long endTime=System.currentTimeMillis();
                    long spentTime=(endTime-startTime)/1000;
                   /* if(spentTime>50) {
                        System.out.println("ip:port为"+connection.getInetAddress().getHostAddress()+":"+connection.getPort()+"的客户端请求超时");
                        break;
                    }*/
                    switch (id.commandID) {
                        case 1:
                            RegistHandle((registration_request_msg) id);
                            break;
                        case 3:
                            loginHandle((login_request_msg) id);
                            break;
                        default:
                            System.out.println("发送的信息id号不对，无法识别对应的id号");
                            break;
                    }
                } catch (SocketException a){
                    cnt++;
                    System.out.println("发生"+cnt+"次SocketException:"+a.getMessage());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("线程"+this.toString()+"结束");
        }

    }
    public static  void config() throws IOException, ClassNotFoundException {

        try {
            file_input=new ObjectInputStream(new FileInputStream(file));
            userlist = (ConcurrentHashMap<String, String>) file_input.readObject();
        }catch(EOFException e)
        {
            userlist=new ConcurrentHashMap<>();
        }
        port=10010;
        backlog=100;
        executor=Executors.newCachedThreadPool();
    }
    public static  void waitForConnection() throws IOException {
        server=new ServerSocket(port,backlog);
        executor.execute(new Timer(null));
       while(true)
       {
           connection=server.accept();
           executor.execute(new workthread(connection));
       }
    }

    public static void main(String[]args) throws IOException, ClassNotFoundException {
         config();
         waitForConnection();
    }

}






















