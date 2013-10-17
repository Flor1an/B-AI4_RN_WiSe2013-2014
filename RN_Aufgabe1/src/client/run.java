package client;

public class run {
	public static void main(String[] args) {
		UserData user1 = new UserData("flbjkl@gmx.de","", "pop.gmx.net", 110);
		
		MailClient c1 = new MailClient(user1);
		
		c1.start();
	
	}
}
