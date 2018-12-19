import javafx.util.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server {
    public static ConcurrentLinkedQueue<Pair<DataOutputStream,Iterator>> sender=new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Integer> history=new ConcurrentLinkedQueue<>();
    public static ExecutorService  pool=Executors.newFixedThreadPool(8);
    static class DealThread implements  Runnable{
        @Override
        public void run() {
            Timer timer=new Timer();
            MyTimerTask task=new MyTimerTask("background_thread");
            timer.schedule(task,0,250);
        }
    }
    static  class PullHistoryThread implements  Runnable{
        Socket connection=null;
        DataOutputStream output=null;
        public PullHistoryThread(Socket connection){
            this.connection=connection;
        }
        @Override
        public void run() {
            try {
                output=new DataOutputStream(connection.getOutputStream());
                Iterator iter=history.iterator();
                while(iter.hasNext()) {
                    output.writeInt((Integer) iter.next());
                    output.flush();
                }

                sender.add(new Pair<>(output,iter));
            } catch (IOException e) {
                System.out.println("往"+connection.getInetAddress().getHostName()+"推送历史信息时出现问题");
                e.printStackTrace();
            }
        }
    }
    public static void main(String[]args) {
        ServerSocket server= null;
        try {
            server = new ServerSocket(12345);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        pool.execute(new DealThread());
        Socket connection=null;
        while (true){
            try {
                connection=server.accept();
            } catch (IOException e) {
                continue;
            }
            pool.execute(new PullHistoryThread(connection));
        }

    }
}
class MyTimerTask extends TimerTask{
    String task_name=null;
    public MyTimerTask(String task_name){
        this.task_name=task_name;
    }
    @Override
    public void run() {
        int value=new Random().nextInt(101);
        Server.history.add(value);
        System.out.println(value);
        for(Pair<DataOutputStream,Iterator> one:Server.sender )
        {
            Server.pool.execute(new SubThread(one,value));
        }

    }
    class SubThread implements  Runnable{
        DataOutputStream out=null;
        Iterator it=null;
        int value;
        public SubThread(Pair<DataOutputStream,Iterator> tmp,int value){
            this.out=tmp.getKey();
            this.it=tmp.getValue();
            this.value=value;
        }
        @Override
        public void run() {
            try {
                while(it.hasNext())
                {
                    out.writeInt((Integer)it.next());
                }
                out.writeInt(value);
                out.flush();
            }
            catch (SocketException e){
                System.out.println("one user is offline in abnormal ways");
                Server.sender.remove(out);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
