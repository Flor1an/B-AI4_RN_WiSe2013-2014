package client;


import helper.MailWriter;
import helper.UserData;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Pop3Client Klasse
* */
public class Pop3Client extends Thread {

    /**
    * Initialisierung der Instanzvariablen
    * */
	private Socket clientSocket; // Socket
	private DataOutputStream outToServer; // Ausgabestream zum Server
	private BufferedReader inFromServer; // Eingabestream vom Server
	private boolean serviceRequested = true; // Client beenden?
    private UserData user;

    /**
    * Konstruktor
    * @param Benutzername vom Typ UserData
    * */
	public Pop3Client(UserData user) {
		this.user = user;
	}

    /**
    * Thream erstellen
    * @return void
    * */
	public void run() {
		 while (true){
			fetchingEmails();
			try {
				writeLogFile("Fetching completed. Waiting 30sek...\n");
				System.out.println();
				Thread.sleep(10000);
				writeLogFile("Starting Fetching");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

    /**
    * Ruft die Emails vom Server ab
    * @return void
    * */
	private void fetchingEmails() {

		/* Ab Java 7: try-with-resources mit automat. close benutzen! */
		try {
			/* Socket erzeugen --> Verbindungsaufbau mit dem Server */
			clientSocket = new Socket(user.getServerAdress(), user.getPort());

			/* Socket-Basisstreams durch spezielle Streams filtern */
			outToServer = new DataOutputStream( clientSocket.getOutputStream( ));
			inFromServer = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()) );

            /** valid ist der Rückgabewert der einzelnen Merhoden */
			boolean valid = false;

            valid = serverStatus();

            /** Benutzer ok? */
			if (valid) {
				valid = fireUSER();
			}

            /** Passwort ok? */
			if (valid) {
				valid = firePASS();
			}

            /** Benutzer und Passwort ok */
			if (valid) {
//				writeToServer("LIST");
//
//                readFromServer();

				int amountOfMails = getAmountOfMailsByFireSTAT();

				if (amountOfMails > 0) {
					saveMailsLocalyByFireingUIDLandRETRandDELE(amountOfMails);
				}
			}

            /** Verbindung schließen */
			closeConnection();
		} catch (IOException e) {
			System.err.println("Connection aborted by server!");
		}

		
	}

    /**
    * Sendet Befehl an den Server
    * @param Server Anfrage als String
    * */
	private void writeToServer(String request) throws IOException {
		/* Sende eine Zeile zum Server */
		outToServer.writeBytes(request + '\n');
		System.out.println("\n>>POP3 Client requested from " + user.getServerAdress() + ":\t" + request);
		writeLogFile("==> " + request);
	}

    /**
    * Lese Server Antwort
    * @return Server Antwort als String
    * */
	private String readFromServer() throws IOException {
		String reply="";
		try {
			do{
				reply = inFromServer.readLine();

				if (reply.startsWith("+OK") | reply.startsWith("-ERR") | reply.equals(".") | Character.isDigit(reply.charAt(0))) {// ignores mail content
					System.out.println("<<POP3 Client got from " + user.getServerAdress() + ":\t\t" + reply);
					writeLogFile("<== " + reply +"\r\n");
				} else {
					//System.err.println("<<POP3 Client got UNKNOWN from " + user.getServerAdress() + ":\t" + reply);
				}

				
			}while(inFromServer.ready()); //es folgen noch zeilen


		} catch (Exception e) {
			System.err.println("SOMETHING WENT WRONG");
		}
	
		return reply;
	}

    /**
    *
    * */
	private String readFromMailServer() throws IOException {
		String reply="";
		reply = inFromServer.readLine();
		return reply;
	}

	/**
	 * creates and updates an log file with TimeStamp ServerName LogMessage
	 * 
	 * @param text
	 *            input string
	 */
	private void writeLogFile(String text) {
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");

		try {
			MailWriter.writeLog("CLIENT LOG ",ft.format(date) + "\t " + user.getServerAdress() + "\t "+ text + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    /*
    * Schließe Verbindung zum Server
    * */
	private void closeConnection() throws IOException {
		writeToServer("QUIT");
        readFromServer();
        clientSocket.close();
		writeLogFile("Connection closed");
		}

    /*
    * Prüft ob die Antwort vom Server in Ordnung ist und schließt ggf. die Verbindung
    * @return Serverstatus als Boolean
    * */
	private boolean serverStatus() throws IOException {
		String reply = readFromServer();
		boolean status = false;
		if (reply.contains("+OK")) {
			status = true;
		} else {
			closeConnection();
			status = false;
		}
		return status;
	}

    /*
    * Existiert ein Benuter mit der Kennung des gegebenen UserData Objects
    * @return Existenz des Benutzers als boolean
    * */
	private boolean fireUSER() throws IOException {

		String reply = "";
		boolean statusLogin = false;
		writeToServer("USER " + user.getUserName());
		reply = readFromServer();

		if (reply.contains("-ERR")) {
			closeConnection();
			statusLogin = false;
		} else {
			statusLogin = true;
		}

		return statusLogin;

	}

    /*
    * Prüft on ein Benuter mit dem Passwort existiert
    * @return Existenz des Benutzers mit Passwort als boolean
    * */
	private boolean firePASS() throws IOException {

		String reply = "";
		boolean statusLogin = false;
		writeToServer("PASS " + user.getPassword());
		reply = readFromServer();

		if (reply.contains("-ERR")) {
			closeConnection();
			statusLogin = false;

		} else {
			statusLogin = true;
		}

		return statusLogin;

	}

    /*
    * Liefert die Anzahl der vorhandenen EMails auf dem Server samt größe
    * @return Die Anzahl der vorhandenen Mails
    * */
	private int getAmountOfMailsByFireSTAT() throws IOException {

		writeToServer("STAT");

		String reply = readFromServer();
		if (reply.startsWith("-ERR")) {
			System.out.println("Error while reading");
			closeConnection();
		}

		int amount = Integer.parseInt(reply.split(" ")[1]);
		return amount;
	}

    /*
    * Holt alles Mails vom Server ab und löscht diese dort.
    * Außerdem werden die Mails lokal abgelegt
    * */
	private void saveMailsLocalyByFireingUIDLandRETRandDELE(int amount)	throws IOException {
		String reply = "";
		String uidlOfMail = "";

		for (int i = 1; i <= amount; i++) {
			// firering UIDL

			writeToServer("UIDL " + i); 

			reply = readFromServer(); 

			uidlOfMail = reply.split(" ")[2];
			
			
			// firering RETR

			writeToServer("RETR " + i);
			reply = readFromMailServer();

			if (reply.startsWith("-Err")) {
				closeConnection();
			} else {

				while (!(reply = readFromMailServer()).equals(".")) {
					if (reply.startsWith("..") && reply.length() > 1){
						reply = reply.substring(1);
					}					

					MailWriter.writeMail(uidlOfMail, reply);

				}
			}
			
			//firering DELE
			if(user.keepCopyOnServer()==false){
				writeToServer("DELE " + i);
				readFromServer();
			}
		}

		
	}

}
