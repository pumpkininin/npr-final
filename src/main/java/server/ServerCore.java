package server;

import data.Message;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.security.Provider;
public class ServerCore{
    private static final String KEY_STORE_PATH = "SSLStore";
    private static final String KEY_STORE_PW = "nprfinal";
    private HashMap<String, ObjectOutputStream> clientOs;
    private HashMap<String, String> accountSet;
    private HashSet<String> activeSet;
    private SSLServerSocket serverSocket;
    private int port;
    private JTextArea console;
    private DefaultListModel model;
    private ServerGUI serverGUI;
    public ServerCore(int port, JTextArea jTextArea, DefaultListModel model, ServerGUI serverGUI) throws IOException {
        this.port = port;
        this.model = model;
        this.console = jTextArea;
        this.serverGUI = serverGUI;
        clientOs = new HashMap<>();
        accountSet = new HashMap<>();
        activeSet = new HashSet<>();

    }
    static {
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PW);
    }
    public void startServer() throws IOException {
        try{
            // SSLServerSocketFactory for building SSLServerSockets
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            // create SSLServerSocket on specified port
            serverSocket = (SSLServerSocket) socketFactory.createServerSocket(this.port);
            console.append("running");
            console.append(" on port " + port +"\n");
            while (true){
                Socket socket = serverSocket.accept();
                ServerService serverService = new ServerService(socket);
                serverService.start();
            }
        } catch (BindException e){
            serverGUI.notifyBindingException(port);
        }

    }

    public void stopServer() throws IOException {
        Message newMsg = new Message();
        newMsg.setMessageType(Message.MessageType.STOP_SERVER);
        serverSocket.close();
    }

    class ServerService extends Thread{
        private String clientName;
        private Socket clientSocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        public ServerService(Socket socket){
            this.clientSocket = socket;
        }
        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(clientSocket.getInputStream());
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                while (!clientSocket.isClosed()) {
                    Message msg = (Message) ois.readObject();
                    if (msg != null) {
                        switch (msg.getReceiverType()) {
                            case PERSON:
                                transferMessage(msg);
                                break;
                            case GROUP:
                                if (msg.getMessageType() == Message.MessageType.REGISTER) {
                                    register(msg);
                                } else if (msg.getMessageType() == Message.MessageType.LOGIN) {
                                    login(msg);
                                } else if (msg.getMessageType() == Message.MessageType.LOGOUT) {
                                    logout(msg);
                                    notifyToAllUsers(msg.getSender());
                                } else if (msg.getMessageType() == Message.MessageType.MSG) {
                                    transferToAll(msg);
                                } else if (msg.getMessageType() == Message.MessageType.LOGOUT) {
                                    logout(msg);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }catch (SocketException e) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void transferToAll(Message msg) throws IOException {
            for(String key : clientOs.keySet()){
                if(key.equals(msg.getSender())){
                    continue;
                }else{
                    ObjectOutputStream os = clientOs.get(key);
                    os.writeObject(msg);
                    os.flush();
                }

            }
        }

        private void transferMessage(Message msg) throws IOException {
            ObjectOutputStream receiverOs = clientOs.get(msg.getReceiver());
            receiverOs.writeObject(msg);
            receiverOs.flush();
            receiverOs.reset();
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
            updateMsg.setActiveList(new ArrayList<>(activeSet));
            updateMsg.setMessageType(Message.MessageType.UPDATE_LIST);
            updateMsg.setContent(clientName);
            for(Map.Entry<String, ObjectOutputStream> set : clientOs.entrySet()){
                if(set.getKey().equals(clientName)){
                    System.out.println("true");
                    continue;
                }else {
                    updateMsg.setReceiver(set.getKey());
                    set.getValue().writeObject(updateMsg);
                    set.getValue().flush();
                    set.getValue().reset();
                }
            }
        }
        private void register(Message message) throws IOException {
            Message response = new Message();
            for(String s : accountSet.keySet()){
                if(s.equals(message.getSender())){
                    response.setMessageType(Message.MessageType.DUPLICATED_USER);
                    oos.writeObject(response);
                    oos.flush();
                    oos.reset();
                    return;
                }
            }
            clientOs.put(message.getSender(), oos);
            accountSet.put(message.getSender(), message.getContent());
            console.append("New user: " + message.getSender() + " has been registered!\n");

            response.setMessageType(Message.MessageType.REGISTER_SUCCESS);
            oos.writeObject(response);
            oos.flush();
            oos.reset();
//            notifyToAllUsers(message.getSender());
        }
        private void login(Message msg) throws IOException {
            Message response = new Message();
            if(accountSet.get(msg.getSender()).equals(msg.getContent())){
                clientOs.put(msg.getSender(), oos);
                activeSet.add(msg.getSender());
                response.setMessageType(Message.MessageType.LOGIN_SUCCESS);
                console.append(msg.getSender() + " has been login!\n");
                model.addElement(msg.getSender());
                response.setActiveList(new ArrayList<>(activeSet));
                oos.writeObject(response);
                oos.flush();
                notifyToAllUsers(msg.getSender());
            }else{
                response.setMessageType(Message.MessageType.WRONG_USERNAME_PASSWORD);
                oos.writeObject(response);
                oos.flush();
            }
        }
        private void logout(Message msg) throws IOException {
            activeSet.remove(msg.getSender());
            clientOs.remove(msg.getSender());
            clientSocket.close();
            notifyToAllUsers(msg.getSender());
        }
    }

}
