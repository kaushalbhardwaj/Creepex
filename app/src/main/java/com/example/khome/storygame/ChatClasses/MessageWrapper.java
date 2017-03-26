package com.example.khome.storygame.ChatClasses;

/**
 * Created by khome on 26/3/17.
 */

public class MessageWrapper {

    Message message;
    String type;
    String nextNode;
    String subText;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }
}
