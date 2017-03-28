package com.darkwolve.khome.storygame.ChatClasses;

import com.stfalcon.chatkit.commons.models.IUser;



/**
 * Created by khome on 22/2/17.
 */


public class Author implements IUser {

    String id,name;
    String avatar;
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
