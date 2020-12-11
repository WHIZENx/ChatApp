package com.cmu.surrussent.chatapp.app.Chat.Model;

import android.text.Editable;
import android.widget.EditText;

public class Type {

    private String userName;
    private String userId;
    private String viewId;
    private String msg;

    public Type(String userId, String userName, String viewId, String msg) {
        this.userId = userId;
        this.userName = userName;
        this.viewId = viewId;
        this.msg = msg;
    }

    // make sure to have an empty constructor inside ur model class
    public Type() {
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getviewId() {
        return viewId;
    }

    public void setviewId(String viewId) {
        this.viewId = viewId;
    }

    public String getmsg() {
        return msg;
    }

    public void setmsg(String msg) {
        this.msg = msg;
    }

}
