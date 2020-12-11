package com.cmu.surrussent.chatapp.app.Chat.Model;

public class Lover {

    private String userName;
    private String userId;
    private String postKey;

    public Lover(String userId, String userName, String postKey) {
        this.userId = userId;
        this.userName = userName;
        this.postKey = postKey;
    }

    // make sure to have an empty constructor inside ur model class
    public Lover() {
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

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

}

