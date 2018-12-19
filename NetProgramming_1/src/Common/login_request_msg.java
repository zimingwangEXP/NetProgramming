package Common;

public class login_request_msg extends request_msg {
     public login_request_msg(String userName,String passwd){
         super(userName,passwd);
         commandID=3;
     }
}
