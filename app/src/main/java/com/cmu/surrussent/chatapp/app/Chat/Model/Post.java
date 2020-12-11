package com.cmu.surrussent.chatapp.app.Chat.Model;

import com.google.firebase.database.ServerValue;

public class Post {


    private String postKey;
    private String title;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto;
    private String userName;
    private String postDate;
    private int userView;
    private int status;
    private Object timeStamp ;


    public Post(String title, String description, String picture, String userId, String userPhoto, String userName, String postDate, int userView, int status) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.postDate = postDate;
        this.userView = userView;
        this.status = status;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

   // make sure to have an empty constructor inside ur model class
    public Post() {
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserView() {
        return userView;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPostStatus() { return status; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserView(int userView) {
        this.userView = userView;
    }

    public void setPostStatus(int status) {
        this.status = status;
    }

}
