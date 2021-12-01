package data;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private MessageType messageType;
    private List<String> activeList;
    private String content;
    private ReceiverType receiverType;
    private byte[] fileData;
    public Message() {
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public ReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getActiveList() {
        return activeList;
    }

    public void setActiveList(List<String> activeList) {
        this.activeList = activeList;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public enum MessageType{
        LOGIN, LOGOUT, MSG, UPDATE_LIST, DUPLICATED_USER, FILE
    }
    public enum ReceiverType{
        PERSON, GROUP
    }

}
