package server;
/*
 * TCPServer.java
 *
 * Version 2.0
 * Autor: M. Hübner HAW Hamburg (nach Kurose/Ross)
 * Zweck: TCP-Server Beispielcode:
 *        Bei Dienstanfrage einen Arbeitsthread erzeugen, der eine Anfrage bearbeitet:
 *        einen String empfangen, in Großbuchstaben konvertieren und zurücksenden
 */
import helper.MailReader;
import helper.MailWriter;
import helper.UserData;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Pop3Server {
	/* Server, der Verbindungsanfragen entgegennimmt */
	public static final int SERVER_PORT = 11000;
	
	public Pop3Server() {
		ServerSocket welcomeSocket; // TCP-Server-Socketklasse
		Socket connectionSocket; // TCP-Standard-Socketklasse

		int counter = 0; // Zählt die erzeugten Bearbeitungs-Threads

		try {
			/* Server-Socket erzeugen */
			welcomeSocket = new ServerSocket(SERVER_PORT);

			while (true) { // Server laufen IMMER
				System.out.println("Mail Server: Waiting for connection - listening TCP port "	+ SERVER_PORT);
				/*
				 * Blockiert auf Verbindungsanfrage warten --> nach
				 * Verbindungsaufbau Standard-Socket erzeugen und
				 * connectionSocket zuweisen
				 */
				connectionSocket = welcomeSocket.accept();

				/* Neuen Arbeits-Thread erzeugen und den Socket übergeben */
				(new TCPServerThread(++counter, connectionSocket, null)).start();
			}
		} catch (IOException e) {
			System.err.println(this.toString() +  " " +e.toString());
		}
	}


}

class TCPServerThread extends Thread {
	/*
	 * Arbeitsthread, der eine existierende Socket-Verbindung zur Bearbeitung
	 * erhält
	 */
	private int name;
	private Socket socket;

	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	
	private String USERNAME = "flo@flo.de";
	private String PASSWORD = "PASSWORD";		

	boolean serviceRequested = true; // Arbeitsthread beenden?
	MailReader mr = new MailReader();
	
	/**
	 * 0=not logged in
	 * 1=user = ok (now passwort required)
	 * 2=all fine. user "logedin"
	 */
	private int authentificationStatus = 0; 

	public TCPServerThread(int num, Socket sock, UserData user) {
		/* Konstruktor */
		this.name = num;
		this.socket = sock;
	}

	public void run() {
		String reply;


		try {
			/* Socket-Basisstreams durch spezielle Streams filtern */
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
			
			writeToClient("+OK POP3 server ready");

			while (serviceRequested) {
	
				reply = readFromClient();

				writeToClient(reply);


			}
			
			/* Socket-Streams schließen --> Verbindungsabbau */
			writeLogFile("Connection closed.");
			socket.close();
		} catch (IOException e) {
			System.out.println("\nConnection aborted by client!");
		}
		
		writeLogFile("Thread " + name + " stopped!");
		System.out.println("POP3 Server Thread " + name + " stopped!");
	}

	private String readFromClient() throws IOException {
		/* Lies die nächste Anfrage-Zeile (request) vom Client */
		String request = inFromClient.readLine();
		String reply = "";
		System.out.println("\n<<POP3 Server got " + "" + ":\t" + request);
		writeLogFile("<== " + request );
			
		
		if (request==null){
			System.out.println("ERROR: COMMAND NOT CORRECT TRANSFERED -- Command 'null' was recived from client");
			return "-ERR";
		}
		if (request.toUpperCase().startsWith("QUIT")){
			reply=quitMethod(request);
		}else if (authentificationStatus==2){//user is propper "loggedin"
			if (request.toUpperCase().startsWith("STAT")){
				reply = statMethod(request);
			}else if (request.toUpperCase().startsWith("LIST")){
				reply = listMethod(request); 
			}else if (request.toUpperCase().startsWith("RETR")){
				reply = retrMethod(request); 
			}else if (request.toUpperCase().startsWith("DELE")){
				reply = deleMethod(request); 
			}else if (request.toUpperCase().startsWith("NOOP")){
				reply = noopMethod(request); 
			}else if (request.toUpperCase().startsWith("RSET")){
				reply = rsetMethod(request); 
			}else if (request.toUpperCase().startsWith("UIDL")){
				reply = uidlMethod(request); 
			}else{
				System.err.println("Unknown Command");
				reply = "UNKNOWN";
			}
		}else if (request.toUpperCase().startsWith("USER") && authentificationStatus==0){
			reply=userMethod(request);
		}else if (request.toUpperCase().startsWith("PASS") && authentificationStatus==1){
			reply=passMethod(request);
		}else if(request.toUpperCase().startsWith("CAPA")) {
			return "-ERR";
		}else{
			System.out.println("Authentification Problem (maybe unknown command) ###################");	
			reply = "UNKNOWN";
		}
			
	
		
		return reply;
	}

	private void writeToClient(String reply) throws IOException {
		/* Sende den String als Antwortzeile (mit newline) zum Client */
		
		String lines[] = null;
		lines = reply.split("\\r?\\n");
	
		for (String line : lines) {
			outToClient.writeBytes(line + '\n');
			
			
			boolean isNumeric = line.length()>0 ? Character.isDigit(line.charAt(0)) : false;
			
			if (line.startsWith("+OK") | line.startsWith("-ERR") | line.equals(".") | isNumeric ){ //ignores mail content
				System.out.println(">>POP3 Server sais " + "" + ":\t" + line);
				writeLogFile("==> " + line + "\r\n");
			}
		}
		
		
		
		

	}
	

	
	/**
	 * POP3 USER Method
	 * @param input
	 * @return either +OK or -ERR followed by a brief information
	 */
	private String userMethod(String input){
		String reply="";
		if (input.substring(4).trim().equals(USERNAME)&&authentificationStatus==0){
			authentificationStatus=1;
			reply= "+OK ohh good i know this user";
		}else{
			authentificationStatus=0;
			reply="-ERR who is this guy?";
		}
			
		return reply;
	}
	
	/**
	 * POP3 PASS Method
	 * 
	 * @param input
	 * @return either +OK or -ERR followed by a brief information
	 */
	private String passMethod(String input){
		String reply="";
		if (input.substring(4).trim().equals(PASSWORD)&&authentificationStatus==1){
			authentificationStatus=2;
			reply= "+OK user password combination is valid";
		}else{
			authentificationStatus=1;
			reply="-ERR wrong password";
		}
			
		return reply;
	}
	
	/**
	 * POP3 STAT Method
	 * 
	 * @param input
	 * @return either +OK followed by a amout + size
	 */
	private String statMethod(String input){
		String reply="+OK";
		
		
		//response consists of "+OK" followed by a single space, the number of messages in the maildrop, a single space, and the size of the maildrop in octets.
		reply += " " + mr.getAllMailAmount() + " " + mr.getAllMailSize();
		
		
		return reply;
	}
	
	/**
	 * POP3 LIST Method
	 * 
	 * @param input
	 * @return either +OK or -ERR followed by local mail ID + size
	 */
	private String listMethod(String input){
		String reply="";
		try {
			
			if(input.substring(4).trim().equals("")){ //without parameter
				reply = "+OK "+ mr.getAllMailAmount() +" messages ("+ mr.getAllMailSize() + " octets)";
				
				for(int i =0;i<mr.getAllMailAmount();i++){
					reply +="\n" +  (i+1) + " " + mr.getSpecificMailSize(i);
				}
				
				reply +="\n."; //list done
				
			}else{ //with parameter
				int mailId =Integer.parseInt(input.substring(4).trim());
				reply = "+OK " + mailId + " " + mr.getSpecificMailSize(mailId-1);
			}
		
			return reply;
			
			} catch (Exception e) {
				return "-ERR no such message,";
		}
		
	}
	
	/**
	 * POP3 RETR Method
	 * 
	 * @param input
	 * @return either +OK or -ERR followed by content of mail
	 */
	private String retrMethod(String input){
		String reply="";
		try {
			int mailId =Integer.parseInt(input.substring(4).trim());
			reply = "+OK " + mr.getSpecificMailSize(mailId-1) + " octets";
			reply += "\n" + mr.getSpecificMail(mailId-1);
			reply += "\n."; //final
			return reply;
		} catch (Exception e) {
			return "-ERR ohh ohh, cant find this message :O ";
		}
		
	}
	
	/**
	 * POP3 DELE Method
	 * 
	 * @param input
	 * @return either +OK or -ERR followed a brief information
	 */
	private String deleMethod(String input){
		String reply="";
		try {
			int mailId =Integer.parseInt(input.substring(4).trim());
			mr.markAsDeleted(mailId-1);
			reply = "+OK message" + mailId + " deleded (marked as)";
			
			return reply;
		} catch (Exception e) {
			return "-ERR no such message,";
		}

	}
	
	/**
	 * POP3 NOOP Method
	 * 
	 * @param input
	 * @return either +OK 
	 */
	private String noopMethod(String input){
		String reply="+OK";
		return reply;
	}
	
	/**
	 * POP3 RSET Method
	 * 
	 * @param input
	 * @return either +OK 
	 */
	private String rsetMethod(String input){
		String reply="+OK";
		mr.resetDeletionList();
		return reply;
	}
	
	/**
	 * POP3 UIDL Method
	 * 
	 * @param input
	 * @return either +OK or -ERR followed by unique id of mail
	 */
	private String uidlMethod(String input){
		String reply="";
		try {
			
			if(input.substring(4).trim().equals("")){ // with parameter
				reply = "+OK";
				
				for(int i =0;i<mr.getAllMailAmount();i++){
					reply +="\n" +  (i+1) + " " + mr.getSpecificUniqueid(i);
				}
				
				reply +="\n."; //list done
				
			}else{//without parameter
				int mailId =Integer.parseInt(input.substring(4).trim());
				reply = "+OK " + mailId + " " + mr.getSpecificUniqueid(mailId-1);
			}
		
			return reply;
			} catch (Exception e) {
				return "-ERR no such message,";
		}
	}
	
	/**
	 * POP3 QUIT Method
	 * 
	 * @param input
	 * @return either +OK 
	 */
	private String quitMethod(String input){
		String reply="+OK bye bye (marked messages got deleted)";
		
		mr.doDeletion();
		serviceRequested=false;
		return reply;
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
			MailWriter.writeLog("SERVER LOG ", ft.format(date)  +"\t "+ "" +"\t "+ text + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
