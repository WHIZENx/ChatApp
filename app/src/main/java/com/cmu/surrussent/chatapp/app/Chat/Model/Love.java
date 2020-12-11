package com.cmu.surrussent.chatapp.app.Chat.Model;

public class Love {

    private String userName;
    private String userId;
    private String postKey;
    private int check_click;

    public Love(String userId, String userName, String postKey, int check_click) {
        this.userId = userId;
        this.userName = userName;
        this.postKey = postKey;
        this.check_click = check_click;
    }

    // make sure to have an empty constructor inside ur model class
    public Love() {
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

    public int getcheck_click() {
        return check_click;
    }

    public void setcheck_click(int check_click) {
        this.check_click = check_click;
    }

}
