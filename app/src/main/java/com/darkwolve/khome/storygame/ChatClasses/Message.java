package com.darkwolve.khome.storygame.ChatClasses;

import android.support.annotation.Keep;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;


/**
 * Created by khome on 22/2/17.
 */


public class Message implements IMessage {
    String id,text;
    Author author;
    Date createdAt;
    public Message()
    {


    }
    public Message(Message m)
    {
        setId(m.getId());
        setText(m.getText());
        setAuthor(m.getUser());
        setCreatedAt(m.getCreatedAt());

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }




}
