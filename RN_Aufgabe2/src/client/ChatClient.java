package client;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatClient {

    public final Integer PORT = 50000;

    private Login login;
    private Chat chat;
    private TCP tcp;
    private UDP udp;

    public static void main(String[] args){
        new ChatClient();
    }
    public ChatClient(){

        this.login = new Login();

        this.login.setAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ChatClient.this.login.validation()){

                    ChatClient.this.login.dispose();
                    ChatClient.this.chat = new Chat();

                    String host = ChatClient.this.login.getHost();
                    Integer port = ChatClient.this.PORT;
                    String user = ChatClient.this.login.getUser();

                    /* Starte TCP */
                    final TCP t1 = new TCP(ChatClient.this.chat, host, port, user);
                    t1.start();

                    /* Starte UDP Thread */
                    final UDP t2 = new UDP(ChatClient.this.chat, user, t1);
                    t2.start();
                    
                    /* Close User */
                    ChatClient.this.chat.getBye().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            t1.close();
                            t2.close();
                        }
                    });
                    

                }
            }
        });
    }
}
