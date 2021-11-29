package data;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private Type type;
    private List<String> activeList;
    private String content;

    public Message() {
    }

    public Message(String sender, String receiver, Type type, List activeList, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.activeList = activeList;
        this.content =  content;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type{
        LOGIN, LOGOUT, MSG, FILE, UPDATE_LIST, DUPLICATED_USER
    }
}
