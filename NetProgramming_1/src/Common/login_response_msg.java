package Common;

public class login_response_msg extends response_msg{
     public login_response_msg(String status,String description){
         super(status,description);
         commandID=4;
     }
}
