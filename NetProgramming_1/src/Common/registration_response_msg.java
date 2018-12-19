package Common;

public class registration_response_msg  extends response_msg{
     public registration_response_msg(String status,String description){
         super(status,description);
         commandID=2;
     }
}
