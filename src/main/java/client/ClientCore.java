package client;

import data.Message;

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

    public ClientCore( int port ) throws IOException {
        clientSocket = new Socket("localhost", port);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ois = new ObjectInputStream(clientSocket.getInputStream());
    }
    class ClientService extends Thread{
        @Override
        public void run() {
            try {
                while(clientSocket.isConnected()){
                    Message message = (Message) ois.readObject();
                    if(message != null){
                        switch (message.getMessageType()){
                            case DUPLICATED_USER:
//                            register();
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
    public void register(String username,String password) throws IOException {
        this.clientName = username;
        Message loginMsg = new Message();
        loginMsg.setSender(clientName);
        loginMsg.setMessageType(Message.MessageType.LOGIN);
        loginMsg.setReceiverType(Message.ReceiverType.GROUP);
        oos.writeObject(loginMsg);
        oos.flush();
    }
}
