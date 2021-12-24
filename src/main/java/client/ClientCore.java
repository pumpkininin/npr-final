package client;

import data.FileObject;
import data.Message;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCore{
    private String clientName;
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private FileInputStream fileInputStream;
    private int port;
    private List<String> active;
    private LoginGUI loginFrame;
    private ChatGUI chatGUI;
    private HashMap<String, FileObject> fileObjects = new HashMap<>();
    public ClientCore(LoginGUI jFrame){
        this.loginFrame = jFrame;
    }
    public void startClient() throws IOException {
        this.clientSocket = new Socket("localhost", 9999);
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
        this.chatGUI = new ChatGUI(this,active);
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
    public String getClientName(){
        return clientName;
    }
    public void sendMessage(Message message) throws IOException {
        if(message.getMessageType()== Message.MessageType.FILE){
            this.fileObjects.put(message.getFileId(),new FileObject(message.getFileId(), message.getContent(), message.getFileData()));
        }
         oos.writeObject(message);
         oos.flush();

    }
    public void login(String username, String password) throws IOException {
        this.clientName = username;
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
                while(true){
                    Message message = (Message) ois.readObject();
                    if(message != null){
                        switch (message.getMessageType()){
                            case DUPLICATED_USER:
                                this.loginFrame.notifyDuplicate();
                                break;
                            case MSG:
                                System.out.printf("message from %s to you with content: %s", message.getSender(), message.getContent());
                                chatGUI.updateMsg(message);
                                break;
                            case UPDATE_LIST:
                                active = message.getActiveList().stream().filter(user -> !user.equals(this.clientName)).collect(Collectors.toList());
                                chatGUI.updateList(active);
                                break;
                            case REGISTER_SUCCESS:
                                this.loginFrame.registerSuccess();
                                break;
                            case LOGIN_SUCCESS:
                                this.loginFrame.setVisible(false);
                                active = message.getActiveList().stream().filter(user -> !user.equals(this.clientName)).collect(Collectors.toList());
                                chatGUI.setName(this.clientName);
                                chatGUI.setVisible(true);
                                chatGUI.updateList(active);
                                break;
                            case FILE:
                                String fileName = message.getContent();
                                byte[] fileData = message.getFileData();
                                String fileId =message.getFileId();
                                FileObject fileObject = new FileObject(fileId, fileName, fileData);
                                fileObjects.put(fileId, fileObject);
                                chatGUI.updateMsg(message);
                            default:
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


    public FileObject getStoredFile(String fileId) {
        return fileObjects.get(fileId);
    }

    public void logout() throws IOException {
        Message newMsg = new Message();
        newMsg.setSender(this.clientName);
        newMsg.setMessageType(Message.MessageType.LOGOUT);
        newMsg.setReceiverType(Message.ReceiverType.GROUP);
        oos.writeObject(newMsg);
        oos.flush();
        oos.close();
        clientSocket.close();
    }
}
