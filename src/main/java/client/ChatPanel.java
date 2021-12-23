/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package client;

import data.FileObject;
import data.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author nkhieu
 */
public class ChatPanel extends javax.swing.JPanel {
    private String name;
    private ClientCore clientCore;
    private boolean isSendingFile = false;
    private byte[] fileByte;
    private Box vertical = Box.createVerticalBox();
    /**
     * Creates new form ChatPanelGUi
     */
    public ChatPanel(String name, ClientCore clientCore) {
        this.clientCore = clientCore;
        this.name = name;
        initComponents();
    }
    public String getName(){
        return name;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        chatPnl = new javax.swing.JPanel();
        msgPanel = new javax.swing.JPanel();
        fileBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        msgBtn = new javax.swing.JButton();

        chatPnl.setLayout(new java.awt.BorderLayout());
        jScrollPane3.setViewportView(chatPnl);

        fileBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        fileBtn.setText("File");
        fileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileBtnActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        msgBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        msgBtn.setText("Send");
        msgBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    msgBtnActionPerformed(evt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        javax.swing.GroupLayout msgPanelLayout = new javax.swing.GroupLayout(msgPanel);
        msgPanel.setLayout(msgPanelLayout);
        msgPanelLayout.setHorizontalGroup(
                msgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(msgPanelLayout.createSequentialGroup()
                                .addComponent(fileBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 692, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(msgBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12))
        );
        msgPanelLayout.setVerticalGroup(
                msgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, msgPanelLayout.createSequentialGroup()
                                .addContainerGap(14, Short.MAX_VALUE)
                                .addGroup(msgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(msgBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fileBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(msgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 856, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(566, 566, 566)
                                .addComponent(msgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 11, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 59, Short.MAX_VALUE)))
        );
    }// </editor-fold>


    private void fileBtnActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        int x = fileChooser.showDialog(this,"Choose file");
        if(x == JFileChooser.APPROVE_OPTION){
            File f = fileChooser.getSelectedFile();
            fileByte = transferFileToByteArray(f);
            isSendingFile = true;
            jTextArea1.setText(f.getName());
        }
    }

    private byte[] transferFileToByteArray(File f) {
        Path path = Paths.get(f.getAbsolutePath());
        try{
            byte[] data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void msgBtnActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
        String msg = jTextArea1.getText();
        Message newMsg = new Message();
        newMsg.setContent(msg);
        if(this.name.equals("ALL")){
            newMsg.setReceiverType(Message.ReceiverType.GROUP);
            newMsg.setSender(clientCore.getClientName());
        }else{
            newMsg.setReceiver(this.name);
            newMsg.setReceiverType(Message.ReceiverType.PERSON);
            newMsg.setSender(clientCore.getClientName());
        }
        if(!isSendingFile && fileByte==null){
            if(msg.equals("")){
                return;
            }
            newMsg.setMessageType(Message.MessageType.MSG);
        }else{;
            newMsg.setMessageType(Message.MessageType.FILE);
            newMsg.setFileData(fileByte);
            newMsg.setFileId(new Date().getTime() + " " + msg.replace(" ", "-"));
            isSendingFile = false;
        }
        JPanel p2 = formatLabel(newMsg, "");
        chatPnl.setLayout(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        right.add(p2, BorderLayout.LINE_END);
        vertical.add(right);
        vertical.add(Box.createVerticalStrut(15));
        chatPnl.add(vertical, BorderLayout.PAGE_START);
        clientCore.sendMessage(newMsg);
        jTextArea1.setText("");
        validate();
    }

    public void appendNewMsg(Message msg, String sender){
        JPanel p2 = formatLabel(msg, sender);
        JPanel left = new JPanel(new BorderLayout());
        left.add(p2, BorderLayout.LINE_START);
        vertical.add(left);
        vertical.add(Box.createVerticalStrut(15));
        chatPnl.add(vertical, BorderLayout.PAGE_START);
        validate();
    }

    private JPanel formatLabelFile() {
        return new JPanel();
    }
    private JPanel formatLabel(Message msg, String sender){
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
        JLabel l1 = new JLabel("<html><p style = \"width : 150px\">"+msg.getContent()+"</p></html>");
        l1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        l1.setBackground(new JList<>().getSelectionBackground());
        l1.setForeground(new JList<>().getSelectionForeground());
        l1.setOpaque(true);
        l1.setBorder(new EmptyBorder(15,15,15,50));
        JLabel l2 = new JLabel();
        l2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if(sender ==""){
            l2.setText(sdf.format(cal.getTime()));
        }else{
            l2.setText(sender+" ("+sdf.format(cal.getTime())+")");
        }
        p3.add(l2);
        if(msg.getMessageType() == Message.MessageType.MSG){
            p3.add(l1);
        }else{
            JPanel p4 = new JPanel();
            p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
            p4.setBackground(new JList<>().getSelectionBackground());
            JButton downloadBtn = new JButton("Download!");
            downloadBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FileObject file = clientCore.getStoredFile(msg.getFileId());

                    JFrame parentFrame = new JFrame();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Dowload file");
                    fileChooser.setSelectedFile(new File(file.getName()));

                    int userSelection = fileChooser.showSaveDialog(parentFrame);


                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
                            fileOutputStream.write(file.getContent());
                            fileOutputStream.close();
                            JOptionPane.showConfirmDialog(null,
                                    "Download successful", "Message", JOptionPane.DEFAULT_OPTION);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            p4.add(l1);
            p4.add(downloadBtn);
            p3.add(p4);
        }

        return p3;
    }

    // Variables declaration - do not modify
    private javax.swing.JPanel chatPnl;
    private javax.swing.JButton fileBtn;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton msgBtn;
    private javax.swing.JPanel msgPanel;
    // End of variables declaration
}
