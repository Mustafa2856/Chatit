package com.example.chatit;

import android.util.Pair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chats {
    Map<String,String> usernames = new HashMap<>();
    Map<String,Pair<Timestamp,String>> display_list = new HashMap<>();
    Map<String,ArrayList<Pair<Timestamp,String>>> messages = new HashMap<>();

    public void addMessage(String uname,String email,String message,Timestamp tmp){
        if(usernames.get(email)==null){
            usernames.put(email,uname);
            messages.put(email,new ArrayList<>());
            messages.get(email).add(new Pair<>(tmp,message));
            display_list.put(email,new Pair<>(tmp,message));
        }else{
            messages.get(email).add(new Pair<>(tmp,message));
            if(display_list.get(email).first.compareTo(tmp)<=0)
                display_list.put(email,new Pair<>(tmp,message));
        }

    }

}
