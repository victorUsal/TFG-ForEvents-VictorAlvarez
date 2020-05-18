package com.victor.forevents.model;

public class Rating {
    private String user;
    private String postId;
    private String rateValue;


    public Rating(String user, String postId, String rateValue) {
        this.user = user;
        this.postId = postId;
        this.rateValue = rateValue;
    }

    public Rating() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

}
