package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class TCP extends java.lang.Thread {

    private User user;
    private Socket socket;
    private Console console;

    private BufferedReader in;
    private DataOutputStream out;

    public TCP(Socket sock, Console console) {
        this.socket = sock;
        this.console = console;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {}
    }

    public void run() {
        
        String msg = this.read();

        if(msg.matches("^NEW\\s([a-zA-Z0-9]+)$")){
            /* Create User */
            String name = msg.substring(4);
            String host = socket.getInetAddress().toString().substring(1);
            this.user = new User(name, host);
            ChatServer.addUser(this.user);
            this.write("OK");
        }

        while((msg = this.read()) != null){

            if(msg.equals("INFO")){
                this.write("LIST " + ChatServer.getSize() + " " + ChatServer.getUser());
            }else if(msg.equals("BYE")){
                this.write("BYE");
                ChatServer.delUser(this.user);
                break;
            }else{
                this.write("ERROR Unknowen command '" + msg + "'");
                ChatServer.delUser(this.user);
                break;
            }
        }

        /* Close connection */
        this.close();

    }

    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void write(String msg){
        try {
            this.out.writeBytes( msg + "\n" );
            this.console.log( "--> " + msg + " [an " + this.user + "]" );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String read(){
        try {
            String msg = this.in.readLine();
            this.console.log( "<-- " + msg + " [von " + this.user + "]" );
            return msg;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
