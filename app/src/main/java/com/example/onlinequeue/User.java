package com.example.onlinequeue;

import java.io.Serializable;

public class User implements Serializable {
    public String email, name, id, token;
    public int quePos;
    public User(){}
    public User(String id,String email)
    {
        this.id = id;
        this.name = "псевдонім";
        this.email = email;
        this.quePos = 0;
        this.token = "noToken";
    }

    public void setName(String value)
    {
        this.name = value;
    }
    public void setQuePos(int x){this.quePos = x;}
    public void setToken(String tkn){this.token = tkn;}
}


