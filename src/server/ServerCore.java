package server;

import data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerCore implements Runnable{
    private HashMap<String, ObjectOutputStream> clientOs;
    private HashSet<String> clients;
    private ServerSocket serverSocket;
    private int port;

    public ServerCore(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = new HashSet<>();
        clientOs = new HashMap();
    }

    @Override
    public void run() {
        while (true){
            Socket clientSocket = null;
            try{
                clientSocket = serverSocket.accept();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class ServerService extends Thread{
        private String clientName;
        private Socket clientSocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        ServerService(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try{
                ois = new ObjectInputStream(clientSocket.getInputStream());
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                while(true){
                    Message msg = (Message) ois.readObject();
                    if(msg != null){
                        switch (msg.getMessageType()){
                            case LOGIN:
                                if(!isExistUsername(msg.getSender())){
                                    clientName = msg.getSender();
                                    clientOs.put(clientName, oos);
                                    clients.add(clientName);
                                    notifyToAllUsers(clientName);
                                }else{
                                    notifyDuplicate(oos);
                                }
                                break;
                            case MSG:
                                if(msg.getReceiverType() == Message.ReceiverType.PERSON){
                                    transferMessage(msg);
                                }else{
                                    transferToAll(msg);
                                }
                                break;
                            case LOGOUT:
                                notifyToAllUsers(clientName);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void transferToAll(Message msg) throws IOException {
            for(ObjectOutputStream os : clientOs.values()){
                os.writeObject(msg);
                os.flush();
            }
        }

        private void transferMessage(Message msg) throws IOException {
            ObjectOutputStream receiverOs = clientOs.get(msg.getReceiver());
            receiverOs.writeObject(msg);
            receiverOs.flush();
            receiverOs.close();
        }

        private void notifyDuplicate(ObjectOutputStream oos) throws IOException {
            Message duplicatedMSG = new Message();
            duplicatedMSG.setMessageType(Message.MessageType.DUPLICATED_USER);
            oos.writeObject(duplicatedMSG);
            oos.flush();
            oos.reset();
        }

        private void notifyToAllUsers(String clientName) throws IOException {
            Message updateMsg = new Message();
            updateMsg.setSender("SERVER");
            updateMsg.setActiveList(new ArrayList<>(clients));
            updateMsg.setMessageType(Message.MessageType.UPDATE_LIST);
            updateMsg.setContent(clientName);
            for(Map.Entry<String, ObjectOutputStream> set : clientOs.entrySet()){
                updateMsg.setReceiver(set.getKey());
                set.getValue().writeObject(updateMsg);
                set.getValue().flush();
                set.getValue().reset();
            }
        }

        private boolean isExistUsername(String sender) {
            for(String username : clients){
                if(sender.equalsIgnoreCase(username)) return true;
            }
            return false;
        }
    }

}
