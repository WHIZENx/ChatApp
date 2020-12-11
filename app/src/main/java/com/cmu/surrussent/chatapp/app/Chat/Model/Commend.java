package com.cmu.surrussent.chatapp.app.Chat.Model;

public class Commend {

    private String userName;
    private String userImg;
    private String dateId;
    private String commendId;
    private String postKeyId;

    public Commend(String userName, String userImg, String dateId, String commendId, String postKeyId) {
        this.userName = userName;
        this.userImg = userImg;
        this.dateId = dateId;
        this.commendId = commendId;
        this.postKeyId = postKeyId;
    }

    public Commend() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getCommendId() {
        return commendId;
    }

    public void setCommendId(String commendId) {
        this.commendId = commendId;
    }

    public String getPostKeyId() {
        return postKeyId;
    }

    public void setPostKeyId(String postKeyId) {
        this.postKeyId = postKeyId;
    }

}
