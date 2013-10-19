package client;

import helper.UserData;

public class runClient {
	public static void main(String[] args) {
		UserData user1 = new UserData("flbjkl@gmx.de","pw", "pop.gmx.net", 110,false);
		
		Pop3Client c1 = new Pop3Client(user1);
		
		c1.startJob();
	
	}
}
