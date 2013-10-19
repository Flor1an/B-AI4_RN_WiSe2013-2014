package server;

import helper.UserData;

import java.util.ArrayList;



public class runServer {
	public static void main(String[] args) {
		ArrayList users = new ArrayList<>();
		UserData u1 = new UserData("flo@flo.de", "PASSWORD");
		users.add(u1);
		
		
		Pop3Server ms = new Pop3Server();
		
		//MailReader mr = new MailReader();

	
	}
}
