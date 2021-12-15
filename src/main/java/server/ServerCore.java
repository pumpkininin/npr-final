package server;

import data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerCore{
    private HashMap<String, ObjectOutputStream> clientOs;
    private HashMap<String, String> accountSet;
    private HashSet<String> activeSet;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newCachedThreadPool();;//thread pool
    public ServerCore(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientOs = new HashMap<>();
        accountSet = new HashMap<>();
        activeSet = new HashSet<>();
        ServerService clientThread = new ServerService();
        executorService.execute(clientThread);
    }

    class ServerService extends Thread{
        private String clientName;
        private Socket clientSocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        @Override
        public void run() {
            try{
                this.clientSocket =  serverSocket.accept();
                System.out.println("new client");
                ois = new ObjectInputStream(clientSocket.getInputStream());
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                while(true){
                    Message msg = (Message) ois.readObject();
                    if(msg != null){
                        switch (msg.getReceiverType()){
                            case PERSON:
                                transferMessage(msg);
                                break;
                            case GROUP:
                                if(msg.getMessageType() == Message.MessageType.REGISTER){
                                    register(msg);
                                }else if(msg.getMessageType() == Message.MessageType.LOGIN){
                                    login(msg);
                                }else if(msg.getMessageType() == Message.MessageType.LOGOUT){
                                    logout(msg);
                                }else if(msg.getMessageType() == Message.MessageType.MSG){
                                    transferToAll(msg);
                                }
                                break;
                            default:
                                break;
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
            updateMsg.setActiveList(new ArrayList<>(activeSet));
            updateMsg.setMessageType(Message.MessageType.UPDATE_LIST);
            updateMsg.setContent(clientName);
            for(Map.Entry<String, ObjectOutputStream> set : clientOs.entrySet()){
                updateMsg.setReceiver(set.getKey());
                set.getValue().writeObject(updateMsg);
                set.getValue().flush();
                set.getValue().reset();
            }
        }
        private void register(Message message) throws IOException {
            Message response = new Message();
            for(String s : accountSet.keySet()){
                if(s.equals(message.getSender())){
                    response.setMessageType(Message.MessageType.DUPLICATED_USER);
                    oos.writeObject(response);
                    oos.flush();
                }
            }
            clientOs.put(message.getSender(), oos);
            accountSet.put(message.getSender(), message.getContent());
            activeSet.add(message.getSender());
            notifyToAllUsers(message.getSender());
        }
        private void login(Message msg) throws IOException {
            Message response = new Message();
            if(accountSet.get(msg.getSender()) == msg.getContent()){
                clientOs.put(msg.getSender(), oos);
                activeSet.add(msg.getSender());
                notifyToAllUsers(msg.getSender());
            }else{
                response.setMessageType(Message.MessageType.WRONG_USERNAME_PASSWORD);
                oos.writeObject(response);
                oos.flush();
            }
        }
        private void logout(Message msg) throws IOException {
            Message response = new Message();
            notifyToAllUsers(msg.getSender());
        }
    }

}
