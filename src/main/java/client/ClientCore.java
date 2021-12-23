package client;

import data.Message;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientCore{
    private String clientName;
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private FileInputStream fileInputStream;
    private int port;
    private LoginGUI loginFrame;
    public ClientCore(LoginGUI jFrame){
        this.loginFrame = jFrame;
    }
    public void startClient() throws IOException {
        this.clientSocket = new Socket("localhost", 9999);

        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
        listenResponse();
    }
    public void register(String username,String password) throws IOException {
        this.clientName = username;
        Message loginMsg = new Message();
        loginMsg.setSender(clientName);
        loginMsg.setContent(password);
        loginMsg.setMessageType(Message.MessageType.REGISTER);
        loginMsg.setReceiverType(Message.ReceiverType.GROUP);
        oos.writeObject(loginMsg);
        oos.flush();
    }
    public void sendMessage(Message message) throws IOException {
        switch (message.getMessageType()){
            case FILE:
                byte[] file = message.getFileData();
                int bytes = 0;
                byte[] buffer = new byte[4*1024];
                while ((bytes=fileInputStream.read(buffer)) != -1){
                    oos.write(buffer, 0, bytes);
                    oos.flush();
                }
                break;
            case MSG:
                oos.writeObject(message);
                oos.flush();
        }

    }
    public void login(String username, String password) throws IOException {
        Message message = new Message();
        message.setSender(username);
        message.setContent(password);
        message.setMessageType(Message.MessageType.LOGIN);
        message.setReceiverType(Message.ReceiverType.GROUP);
        oos.writeObject(message);
        oos.flush();
    }
    private void listenResponse(){
        Runnable runnable = () -> {
            try {
                while(clientSocket.isConnected()){
                    Message message = (Message) ois.readObject();
                    System.out.println(message.getActiveList());
                    if(message != null){
                        switch (message.getMessageType()){
                            case DUPLICATED_USER:
                                this.loginFrame.notifyDuplicate();
                                break;
                            case MSG:
                                System.out.printf("message from %s to you with content: %s", message.getSender(), message.getContent());
                                break;
                            case UPDATE_LIST:
                                break;
                            case REGISTER_SUCCESS:
                                this.loginFrame.notifySuccess();
                                break;
                            case LOGIN_SUCCESS:
                                this.loginFrame.setVisible(false);
                                System.out.println(message.getActiveList());
                                ChatGUI chatGUI = new ChatGUI(this, message.getActiveList());
                                break;

                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        };
        Thread th = new Thread(runnable);
        th.start();
    }




}
