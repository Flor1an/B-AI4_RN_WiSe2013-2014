package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

/**
 * Klasse zum Schrieben und Empfangen von Nachrichten per UDP
 * Jede Instantz läuft in einem eigenen Thread  
 */
public class UDP extends Thread {


    private final String user;  // Hält den Benutzernamen
    private final TCP thread;   // Hält das TCP Socket

    private final Integer PORT = 50001; // Port an den Nachrichten geschickt und Empfangen werden

    private DatagramSocket udpClient; // Klasse für UDP Client
    private DatagramSocket udpServer;   // Klasse für UDP Server
    private DatagramPacket udpPackage;  // Klasse für UDP Package

    private Chat chat;  // GUI des Chats

    /**
     * Kopnstruktor der Klasse UDP
     * @param chat
     * @param user
     * @param thread 
     */
    public UDP(Chat chat, String user, TCP thread){

        this.chat = chat;
        this.user = user;
        this.thread = thread;

        /* Setzte ein Event auf den Senden Button des GUI */
        this.chat.getSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!UDP.this.chat.getText().equals("")) {
                    try {
                        UDP.this.transmit(UDP.this.chat.getText());
                    } catch (IOException e1) {
                    }
                }
            }
        });
    }

    /**
     * Thread-Methode der Klaswse UDP
     */
    public void run() {

        try {
            String response;
            udpClient = new DatagramSocket();
            udpServer = new DatagramSocket(PORT);
            udpPackage = new DatagramPacket(new byte[123], 123);
            
                        
            /* Warte auf eingfehenden Nachrichten */
            while (! isInterrupted()) {
                udpServer.receive(udpPackage);
                response = new String(udpPackage.getData(), 0, udpPackage.getLength(), "UTF-8");
                this.chat.addOutput(response.trim());
            }
           
           udpClient.close();
        } catch (IOException e) {
              if (! isInterrupted()){
                 e.printStackTrace();
              }
              
        }
     

    }

    /**
     * Sendet eine Nachricht an den gegebenen Port
     * @param message
     * @throws IOException 
     */
    private void transmit(String message) throws IOException {

        byte[] sendData = (this.user + ": " + message).getBytes("UTF-8");
        try {
            for(String host : this.thread.getHosts()){
                udpClient.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), PORT));
            }

        } catch (IOException e) {}
    }
    
    /**
     * Beenden
     */
    public void close(){
        udpServer.close();
        interrupt();
    }


}
