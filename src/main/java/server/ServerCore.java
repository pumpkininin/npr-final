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
    private HashMap<String, ObjectOutputStream> clientOs;//hashmap chứa tên người dùng và outputstream tương ứng
    private HashMap<String, String> accountSet;//hashmap chứa username và password
    private HashSet<String> activeSet;// set chứa tên những người đang hoạt động
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
        //import ssl info
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
                                    handleRegister(msg);
                                } else if (msg.getMessageType() == Message.MessageType.LOGIN) {
                                    handleLogin(msg);
                                } else if (msg.getMessageType() == Message.MessageType.LOGOUT) {
                                    handleLogout(msg);
                                } else if (msg.getMessageType() == Message.MessageType.MSG || msg.getMessageType() == Message.MessageType.FILE) {
                                    transferToAll(msg);
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
                if(key.equals(msg.getSender())){//kiểm tra xem có phải người gửi hay ko
                    continue;//bỏ qua chính người gửi
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
        private void handleRegister(Message message) throws IOException {
            Message response = new Message();
            for(String s : accountSet.keySet()){//check if username is exist
                if(s.equals(message.getSender())){//nếu đã tồn tại
                    response.setMessageType(Message.MessageType.DUPLICATED_USER);
                    oos.writeObject(response);
                    oos.flush();
                    oos.reset();
                    return;
                }
            }
            clientOs.put(message.getSender(), oos);//put username and outputstream
            accountSet.put(message.getSender(), message.getContent());//put username and password
            console.append("New user: " + message.getSender() + " has been registered!\n");

            response.setMessageType(Message.MessageType.REGISTER_SUCCESS);
            oos.writeObject(response);
            oos.flush();
            oos.reset();
//            notifyToAllUsers(message.getSender());
        }
        private void handleLogin(Message msg) throws IOException {
            Message response = new Message();
            if(accountSet.get(msg.getSender()).equals(msg.getContent())){//kiểm tra username và password
                clientOs.put(msg.getSender(), oos);
                activeSet.add(msg.getSender());
                response.setMessageType(Message.MessageType.LOGIN_SUCCESS);
                console.append(msg.getSender() + " has been login!\n");
                model.addElement(msg.getSender());//thêm người dùng vào list
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
        private void handleLogout(Message msg) throws IOException {
            activeSet.remove(msg.getSender());//xóa người dùng khỏi danh sách hoạt động
            clientOs.remove(msg.getSender());
            clientSocket.close();//đóng kết nối giữa người dùng và server
            notifyToAllUsers(msg.getSender());//thông báo tới người dùng vừa mới logout
        }
    }

}
