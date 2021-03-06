/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client;

import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;

import javax.swing.*;
import java.io.IOException;

/**
 *
 * @author nkhieu
 */
public class LoginGUI extends javax.swing.JFrame {
    private ClientCore clientCore;
    /**
     * Creates new form LoginGUI
     */
    public LoginGUI() throws IOException {
        initComponents();
        this.clientCore = new ClientCore(this);
        Thread newThread = new Thread(){
            @Override
            public void run(){
                try {
                    clientCore.startClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        newThread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        usernameTf = new javax.swing.JTextField();
        usernameLbl = new javax.swing.JLabel();
        passLbl = new javax.swing.JLabel();
        passTf = new javax.swing.JPasswordField();
        registerBtn = new javax.swing.JButton();
        loginBtn = new javax.swing.JButton();
        logoPanel = new javax.swing.JPanel();
        logoLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        usernameTf.setText("Username");
        usernameTf.setToolTipText("");

        usernameLbl.setText("Username");

        passLbl.setText("Password");

        passTf.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        passTf.setText("Password");
        passTf.setToolTipText("");

        registerBtn.setText("Register");
        registerBtn.addActionListener(e -> {
            String username = usernameTf.getText();//l???y username ??? textfield
            String password = passTf.getText();//l???y password ??? textfield
            try {
                register(username, password);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        loginBtn.setText("Login");
        loginBtn.addActionListener(e -> {
            String username = usernameTf.getText();
            String password = passTf.getText();
            try {
                clientCore.login(username, password);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/hanu.png"));
        logoLbl.setIcon(icon);
        logoPanel.add(logoLbl);
        javax.swing.GroupLayout logoPanelLayout = new javax.swing.GroupLayout(logoPanel);
        logoPanel.setLayout(logoPanelLayout);
        logoPanelLayout.setHorizontalGroup(
                logoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(logoLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        logoPanelLayout.setVerticalGroup(
                logoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(logoLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(129, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(passLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(passTf, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(usernameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(usernameTf, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(148, 148, 148))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(172, 172, 172)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(233, 233, 233)
                                                .addComponent(logoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(logoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(usernameTf, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(usernameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(passTf, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(passLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(46, 46, 46)
                                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46)
                                .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(54, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>
    private void register(String username, String password) throws IOException {
        this.clientCore.register(username, password);
    }
    public void registerSuccess() throws IOException {
        int input = JOptionPane.showConfirmDialog(null, "Register successfully, Now you can login with this account","Success", JOptionPane.DEFAULT_OPTION);
        if(input == 0){
            this.clientCore.login(usernameTf.getText(), passTf.getText());
        }
    }

    public void notifyDuplicate(){
        int input = JOptionPane.showConfirmDialog(null, "Username has been duplicated! Choose an other one","Error",JOptionPane.CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        FlatArcOrangeIJTheme.setup();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel( new FlatArcOrangeIJTheme() );
                    break;
                }
            }
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginGUI().setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton loginBtn;
    private javax.swing.JLabel logoLbl;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JLabel passLbl;
    private javax.swing.JPasswordField passTf;
    private javax.swing.JButton registerBtn;
    private javax.swing.JLabel usernameLbl;
    private javax.swing.JTextField usernameTf;
    // End of variables declaration
}
