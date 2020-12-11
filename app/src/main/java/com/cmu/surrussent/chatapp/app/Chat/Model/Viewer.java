package com.cmu.surrussent.chatapp.app.Chat.Model;

public class Viewer {

    private String userId;
    private String viewId;

    public Viewer(String userId, String viewId) {
        this.userId = userId;
        this.viewId = viewId;
    }

    // make sure to have an empty constructor inside ur model class
    public Viewer() {
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getviewId() {
        return viewId;
    }

    public void setviewId(String viewId) {
        this.viewId = viewId;
    }


}
