package client;

import data.FileInfo;
import data.Message;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientCore implements Runnable{
    private String clientName;
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private FileInputStream fileInputStream;

    public ClientCore(String clientName, int port ) throws IOException {
        this.clientName = clientName;
        clientSocket = new Socket("localhost", port);

    }
    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            Message loginMsg = new Message();
            loginMsg.setSender(clientName);
            loginMsg.setMessageType(Message.MessageType.LOGIN);
            oos.writeObject(loginMsg);
            oos.flush();
            while(clientSocket.isConnected()){
                Message message = (Message) ois.readObject();
                if(message != null){
                    switch (message.getMessageType()){
                        case DUPLICATED_USER:
                            registerAgain();
                            break;
                        case MSG:
                            System.out.printf("message from %s to you with content: %s", message.getSender(), message.getContent());
                            break;
                        case UPDATE_LIST:
                            break;

                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void sendMessage(Message message) throws IOException {
        switch (message.getMessageType()){
            case FILE:
                byte[] file = message.getFileData();
                int bytes = 0;
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileSize(file.length);
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
    private void registerAgain() throws IOException {
        String newUsername = new String();//get new username from ui
        this.clientName = newUsername;
        Message loginMsg = new Message();
        loginMsg.setSender(clientName);
        loginMsg.setMessageType(Message.MessageType.LOGIN);
        oos.writeObject(loginMsg);
        oos.flush();
    }
}
