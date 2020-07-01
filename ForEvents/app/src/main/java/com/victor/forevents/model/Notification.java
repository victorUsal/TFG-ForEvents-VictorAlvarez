package com.victor.forevents.model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private String tag;
    private boolean isPost;

    public Notification(String userid, String text, String postid,String tag, boolean isPost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.tag = tag;
        this.isPost = isPost;
    }

    public Notification () {

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
