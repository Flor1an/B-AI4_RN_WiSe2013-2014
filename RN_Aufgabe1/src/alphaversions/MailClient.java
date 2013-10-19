package alphaversions;

import helper.MailWriter;
import helper.UserData;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;

public class MailClient extends Thread {

	private Socket clientSocket; // TCP-Standard-Socketklasse

	private DataOutputStream outToServer; // Ausgabestream zum Server
	private BufferedReader inFromServer; // Eingabestream vom Server

	UserData user;

	public MailClient(UserData user) {
		this.user = user;
	}

	@Override
	public void run() {

		try {

			while (true) {
				/* Socket erzeugen --> Verbindungsaufbau mit dem Server */
				clientSocket = new Socket(user.getServerAdress(), user.getPort());

				/* Socket-Basisstreams durch spezielle Streams filtern */
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				readFromServer(false);

				// ******************
				login();
				
				
				writeToServer("LIST");
				readFromServer(true);
				
				
				
				int amoutOfMailsOnServer = countMailsOnServer();
				saveAllMailsFromServer(amoutOfMailsOnServer);
				
				logout();
				
				try {
					System.out.println("Fetching mails completed. Waiting 30sek...\n");
					writeLogFile("Fetching mails completed.\r\n");
					Thread.sleep(30000);
					System.out.println("Fetching mails...");
					writeLogFile("Fetching mails...");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			System.err.println(this.getName() + " Connection aborted by server!");
		}

		System.out.println(this.getName() + "TCP Client stopped!");
	}

	private void writeToServer(String request) throws IOException {
		/* Sende eine Zeile zum Server */
		outToServer.writeBytes(request + '\n');
		System.out.println(">>TCP Client sent to " + user.getServerAdress() + ":\t " + request);
		writeLogFile("==> " + request);

	}

	private String readFromServer(boolean multi) throws IOException {
		/* Lies die Antwort (reply) vom Server */
		String reply = inFromServer.readLine();
	
		if (multi==true){
			while(reply.equals(".")!=true){
				System.err.println("<<TCP Client got from " + user.getServerAdress() + ":\t" + reply);
				writeLogFile("<== " + reply);

				reply = inFromServer.readLine();
			}
		}else{
			if (reply.startsWith("+OK") | reply.startsWith("-ERR")){ //ignores mail content
				System.out.println("<<TCP Client got from " + user.getServerAdress() + ":\t" + reply);
				writeLogFile("<== " + reply);
			}
		}
		
		return reply;
	}
	

	/**
	 * Authorization with POP Server
	 * 
	 * @return Success or failure
	 * @throws Exception
	 */
	private boolean login() throws Exception {
		boolean status = false;
		String answer = "";

		// ##############################################

		writeToServer("USER " + user.getUserName());
		answer = readFromServer(false);

		if (answer.startsWith("-ERR")) {
			status = false;
			logout();
		} else if (answer.startsWith("+OK")) {
			status = true;
		} else {
			throw new LoginException();
		}

		// ##############################################

		writeToServer("PASS " + user.getPassword());
		answer = readFromServer(false);

		if (answer.startsWith("-ERR")) {
			status = false;
			logout();
		} else if (answer.startsWith("+OK")) {
			status = true;
		} else {
			throw new LoginException();
		}

		return status;
	}

	/**
	 * Disconnects from the Server
	 * 
	 * @return Success
	 * @throws Exception
	 */
	private boolean logout() throws Exception {
		writeToServer("QUIT");
		clientSocket.close();/* Socket-Streams schließen --> Verbindungsabbau */

		return true;
	}

	/**
	 * Counts the number on Mails that are currently on the Server
	 * 
	 * @return amount of mails on POP Server
	 * @throws Exception
	 */
	private int countMailsOnServer() throws Exception {
		String answer = "";
		writeToServer("STAT");// stat gibt anzahl mails aufm server zurueck ->
								// string mit oktalzahlen

		answer = readFromServer(false);
		if (answer.startsWith("-ERR")) {
			System.out.println("Error while reading");
			logout();

		}
		String[] subelem = answer.split(" "); // von stat das 2. elem ist anzahl
												// mails, rest unwichtig
		return Integer.parseInt(subelem[1]);

	}

	/**
	 * runs thru all mails on the server. these mails get stored localy and gets
	 * deleted from the server
	 * 
	 * @param amoutOfMailsOnServer
	 * @return
	 * @throws Exception
	 */
	private void saveAllMailsFromServer(int amoutOfMailsOnServer)
			throws Exception {

		for (int i = 1; i <= amoutOfMailsOnServer; i++) {

			String uniqueID = getUniqueIdWithUIDL(i);

			getMailWithRETRandStoreMail(i, uniqueID);
			writeLogFile("    Mail " + uniqueID + " saved localy.");
			
			deletsMailOnServerWithDELE(i);
			writeLogFile("    Mail " + uniqueID + " deleted on Server.");

		}

	}

	/**
	 * Gets the unique-id of ans mail specified by the position in "mail list"
	 * 
	 * @param mailNumber
	 * @return unique-id
	 * @throws Exception
	 */
	private String getUniqueIdWithUIDL(int mailNumber) throws Exception {
		writeToServer("UIDL " + mailNumber);

		String answer = readFromServer(false);

		if (answer.startsWith("-ERR")) {
			logout();
		}

		String[] subelem = answer.split(" "); // [0]Status [1]pos [2]unique-id
		String uniqueID = (subelem[2]);

		return uniqueID;
	}

	/**
	 * gets an specific mail from the server and stores it localy
	 * 
	 * @param mailNumber
	 * @param uniqueID
	 * @throws Exception
	 */
	private void getMailWithRETRandStoreMail(int mailNumber, String uniqueID)
			throws Exception {
		writeToServer("RETR " + mailNumber);
		String answer = readFromServer(true);

		if (answer.startsWith("-ERR")) {
			logout();
		}

		while (!(answer = readFromServer(false)).equals(".")) {
			answer = (answer.startsWith(".") && answer.length() > 1) ? answer
					.substring(1, answer.length()) : answer;
			// System.err.println(answer);

			MailWriter.writeMail(uniqueID, answer);

		}

	}

	/**
	 * delets an specific mail from the server
	 * 
	 * @param mailNumber
	 * @throws Exception
	 */
	private void deletsMailOnServerWithDELE(int mailNumber) throws Exception {
		//writeToServer("DELE " + mailNumber);

		if (readFromServer(false).startsWith("+OK")) {
			System.out.println("Message " + mailNumber + " deleted");
		} else {
			System.out.println("Could not delete message " + mailNumber);
		}
	}
	
	/**
	 * creates and updates an log file with TimeStamp ServerName LogMessage
	 * 
	 * @param text input string
	 */
	private void writeLogFile(String text){
		Date date = new Date();
		 SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");

			     
			      
		try {
			MailWriter.writeLog("CLIENT LOG ", ft.format(date)  +"\t "+user.getServerAdress() +"\t "+ text + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
