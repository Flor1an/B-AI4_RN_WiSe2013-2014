package client;

import helper.UserData;

public class runClient {
	public static void main(String[] args) {
		//UserData user1 = new UserData("flbjkl@gmx.de","simplepass", "pop.gmx.net", 110,true);
		UserData user1 = new UserData("flo@flo.de","flopass", "localhost", 11000,true);
		
		Pop3Client c1 = new Pop3Client(user1);
		//Pop3Client c2 = new Pop3Client(user1);
		c1.start();
		//c2.start();
	
	}
}
