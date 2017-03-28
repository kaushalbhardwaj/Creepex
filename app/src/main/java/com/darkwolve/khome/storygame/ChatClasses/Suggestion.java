package com.darkwolve.khome.storygame.ChatClasses;

import android.support.annotation.Keep;

import proguard.annotation.KeepClassMembers;

/**
 * Created by khome on 17/3/17.
 */

@Keep
@KeepClassMembers
public class Suggestion {
    String text;
    int id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
