package com.example.onlinequeue;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Queue {

    public String nameQueue;
    public int numOfPeople;
    Map<String, Object> admins;


    public Queue(){}

    public Queue (String nameQueue)
    {
        this.nameQueue=nameQueue;
        this.admins = new HashMap<>();
        this.numOfPeople = 0;
    }

    public void setAdmin(String emailAdm)
    {

        admins.put(emailAdm, true);
    }
}
