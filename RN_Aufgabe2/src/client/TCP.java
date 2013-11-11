package client;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TCP extends java.lang.Thread {

    private Chat chat;
    private String host;
    private Integer port;
    private String user;

    private Set<String> hostList;
    private ArrayList<String> userList;

    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    /**
     * Kopnstruktor der Klasse UDP
     * @param chat Instanz des Guis
     * @param host Host
     * @param port Port
     * @param user Benutzer
     */
    public TCP(Chat chat, String host, Integer port, String user){
        this.chat = chat;
        this.host = host;
        this.port = port;
        this.user = user;

        try {
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {}

    }


    /**
     * Thread-Methode der Klaswse UDP
     */
    public void run(){

        try {

            String response;
            this.write("NEW " + user);
            if(this.read().equals("OK")){
                
                while (! isInterrupted()) {
                    this.write("INFO");
                    response = this.read();
                    
                    if(response.startsWith("ERROR")){
                        break;
                    }else if(response.startsWith("LIST")){
                        this.hostList = new HashSet<String>();
                        this.userList = new ArrayList<String>();
                        String user = "";
                        String[] splited = response.substring(7).split(" ");
                        for(Integer i=0; i<splited.length; i++){
                            if(i%2 == 0){
                                this.hostList.add(splited[i]);
                            }else{
                                user += splited[i] + "\n";
                                this.userList.add(splited[i]);
                            }
                        }
                        this.chat.setUsers(user);
                    }

                    try {
                        this.sleep(5000);
                        
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
            this.write("BYE");
            this.read();
            this.socket.close();
            System.exit(0);
        } catch (IOException e) {}
    }

    /**
     * Gibt alle aktuellen Hosts zurück
     * @return Hosts
     */
    public Set<String> getHosts(){
        return this.hostList;
    }


    /**
     * Sendet eine Nachticht
     */
    public void write(String msg){
        try {
            this.out.writeBytes(msg + "\n");
        } catch (IOException e) {}
    }


    /**
     * Ließt eine Nachticht
     */
    public String read(){
        try {
            return this.in.readLine();
        } catch (IOException e) {}
        
        return null;
    }

    
    /**
     * Beenden
     */
    public void close(){
    
            interrupt();
      
    }
}
