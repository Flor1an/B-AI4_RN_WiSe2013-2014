package server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatServer {

    public final Integer PORT = 50000;
    public final Integer BENUTZERLIMUIT = 2;
    public Console console;
    private ArrayList<TCP> thread;
    private static ArrayList<User> userList = new ArrayList();

    public static void main(String[] args) {
        new ChatServer();
    }

    public ChatServer(){
        console = new Console();
        thread = new ArrayList<TCP>();

        /* Close */
        console.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (TCP t : thread){
                    t.close();
                }
                System.exit(0);
            }
        });

        /* startSever */
        startSever();
    }

    public static void addUser(User user){
        userList.add(user);
    }

    public static String getUser(){
        String s = "";
        for(User u : userList){
            s += " " + u.toString();
        }
        return s.substring(1);
    }

    public static Integer getSize(){
        return userList.size();
    }

    public static void delUser(User user){
        Iterator<User> it = userList.iterator();
        while(it.hasNext()){
            User u = it.next();
            if(u.toString().equals(user.toString())){
                it.remove();
                break;
            }
        }
    }

    private void startSever(){
        ServerSocket welcomeSocket;
        Socket connectionSocket;
        int counter = 0;

        try {
            welcomeSocket = new ServerSocket(PORT);
            console.log("Listening Port " + PORT);
            while (true) {
                
                    connectionSocket = welcomeSocket.accept();
                    
        System.out.println(ChatServer.getSize());
        
        if(ChatServer.getSize() < BENUTZERLIMUIT){
           
                    TCP t = new TCP(connectionSocket, console);
                    thread.add(t);
                    t.start();
        }else{
            connectionSocket.close();
        }
        
        
                
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}

