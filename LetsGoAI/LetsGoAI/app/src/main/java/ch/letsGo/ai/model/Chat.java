package ch.letsGo.ai.model;

import java.util.ArrayList;
import java.util.List;


public class Chat {

    public String id;
    public String status;
    public List<Message> messageList = new ArrayList<>();


    public Chat() {
    }

    public Chat(String id) {
        this.id = id;
    }

    public Chat(String status, List<Message> messageList) {
        this.status = status;
        this.messageList = messageList;
    }

    public Chat(String id, String status, List<Message> messageList) {
        this.id = id;
        this.status = status;
        this.messageList = messageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", messageList=" + messageList +
                '}';
    }
}
