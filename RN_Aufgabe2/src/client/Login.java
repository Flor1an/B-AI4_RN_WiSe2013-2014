/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author Florian
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login2
     */
    public Login() {
        initComponents();
        
        /* Window */
        this.setVisible(false);
        this.setTitle("Chat Login");
        try {
            guiUser.setText(System.getProperty("user.name"));
        } catch (Exception e) {
        }
        try {
            guiServer.setText((String) java.net.InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
        }
        
  

        /* Close */
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Login.this.dispose();
            }
        });

        /* Center */
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        /* Show */
        this.setVisible(true);
    }
    
     public void setAction(ActionListener action) {
        this.guiButton.addActionListener(action);
    }

    public Boolean validation() {
        return this.guiUser.getText().matches("[a-zA-Z0-9]+");
    }

    public String getHost() {
        return this.guiServer.getText();
    }

    public String getUser() {
        return this.guiUser.getText();
    }

    private Boolean checkString(String str, String reg) {
        Boolean match = str.matches(reg);
        System.out.println("Check String '" + str + "' (" + match + ")");
        return match;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guiServer = new javax.swing.JTextField();
        guiUser = new javax.swing.JTextField();
        guiButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        guiServer.setText("127.0.0.1");

        guiUser.setText("PeterGoge");

        guiButton.setText("Anmelden");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(guiButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiUser)
                    .addComponent(guiServer))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiButton;
    private javax.swing.JTextField guiServer;
    private javax.swing.JTextField guiUser;
    // End of variables declaration//GEN-END:variables
}