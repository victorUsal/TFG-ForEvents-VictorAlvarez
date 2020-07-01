package com.victor.forevents.model;

import java.util.ArrayList;
import java.util.List;


public class Preferencias {
    private String userid;
    private List<String> categories;

    public Preferencias(String userid) {
        this.userid = userid;
        categories = new ArrayList<>();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
