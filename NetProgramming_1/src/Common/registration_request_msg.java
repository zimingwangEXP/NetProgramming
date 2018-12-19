package Common;

public class registration_request_msg extends request_msg{

    public registration_request_msg(String userName,String passwd){
       super( userName, passwd);
       commandID=1;
    }
}
