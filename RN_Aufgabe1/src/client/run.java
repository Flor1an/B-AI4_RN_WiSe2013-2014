package client;

public class run {
	public static void main(String[] args) {
		UserData user1 = new UserData("flbjkl@gmx.de","Deluxe15)", "pop.gmx.net", 110);
		UserData user2 = new UserData("familiekletz@web.de","kletz123", "pop3.web.de", 110);
		
		MailClient c1 = new MailClient(user1);
		MailClient c2 = new MailClient(user2);
		
		c1.start();
		c2.start();
	
	}
}