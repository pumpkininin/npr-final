package client;

import data.FileObject;
import data.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCore{
    private static final String TRUST_STORE_PATH = "SSLStore";
    private static final String TRUST_STORE_PW = "nprfinal";
    private String clientName;
    private SSLSocket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private FileInputStream fileInputStream;
    private int port;
    private List<String> active;
    private LoginGUI loginFrame;
    private ChatGUI chatGUI;
    private HashMap<String, FileObject> fileObjects = new HashMap<>();//chứa những file đã gửi và nhận
    public ClientCore(LoginGUI jFrame){
        this.loginFrame = jFrame;
    }
    static {
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PW);
    }
    public void startClient() throws IOException {
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // create SSLSocket from factory
        clientSocket = (SSLSocket) socketFactory.createSocket("127.0.0.1", 9999);
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println(oos);
        this.chatGUI = new ChatGUI(this,active);
        listenResponse();
    }
    public void register(String username,String password) throws IOException {
        this.clientName = username;
        Message loginMsg = new Message();
        loginMsg.setSender(clientName);//set username
        loginMsg.setContent(password);//set password
        loginMsg.setMessageType(Message.MessageType.REGISTER);
        loginMsg.setReceiverType(Message.ReceiverType.GROUP);
        oos.writeObject(loginMsg);//gửi đi cho server
        oos.flush();
    }
    public String getClientName(){
        return clientName;
    }
    public void sendMessage(Message message) throws IOException {
        if(message.getMessageType() == Message.MessageType.FILE){
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
                while (clientSocket.isConnected()) {
                    Message message = (Message) ois.readObject();
                    if (message != null) {
                        switch (message.getMessageType()) {
                            case DUPLICATED_USER:
                                this.loginFrame.notifyDuplicate();
                                break;
                            case MSG://tin nhắn thông thường
                                chatGUI.updateMsg(message);//hiện thị tin nhắn
                                break;
                            case UPDATE_LIST:
                                active = message.getActiveList().stream().filter(user -> !user.equals(this.clientName)).collect(Collectors.toList());
                                System.out.println("list:" + active);
                                chatGUI.updateList(active, message.getContent());
                                break;
                            case REGISTER_SUCCESS:
                                this.loginFrame.registerSuccess();
                                break;
                            case LOGIN_SUCCESS:
                                this.loginFrame.setVisible(false);//ẩn cửa sổ login
                                active = message.getActiveList().stream().filter(user -> !user.equals(this.clientName)).collect(Collectors.toList());//loại chính tên người dùng ra khỏi list
                                chatGUI.setName(this.clientName);
                                chatGUI.setVisible(true);//hiện thị chatGUI
                                chatGUI.initList(active);//khởi tạo danh sách list người dùng
                                break;
                            case FILE://gửi nhận file
                                String fileName = message.getContent();//lấy filename
                                byte[] fileData = message.getFileData();//lấy file content
                                String fileId = message.getFileId();
                                FileObject fileObject = new FileObject(fileId, fileName, fileData);
                                fileObjects.put(fileId, fileObject);//lưu file vào fileObjects
                                chatGUI.updateMsg(message);
                            default:
                                break;
                        }
                    }
                }
            }catch (EOFException e){

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
    }
}
