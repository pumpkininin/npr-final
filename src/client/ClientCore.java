package client;

import data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientCore implements Runnable{
    private String clientName;
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

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
            loginMsg.setType(Message.Type.LOGIN);
            oos.writeObject(loginMsg);
            oos.flush();
            while(clientSocket.isConnected()){
                Message message = (Message) ois.readObject();
                if(message != null){
                    switch (message.getType()){
                        case DUPLICATED_USER:
                            registerAgain();
                            break;
                        case MSG:
                            break;
                        case FILE:
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

    private void registerAgain() throws IOException {
        String newUsername = new String();//get new username from ui
        this.clientName = newUsername;
        Message loginMsg = new Message();
        loginMsg.setSender(clientName);
        loginMsg.setType(Message.Type.LOGIN);
        oos.writeObject(loginMsg);
        oos.flush();
    }
}
