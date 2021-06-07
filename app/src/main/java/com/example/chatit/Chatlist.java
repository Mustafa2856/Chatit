package com.example.chatit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;


public class Chatlist extends AppCompatActivity {

    TextView name;
    LinearLayout chats;
    Map<String,ArrayList<Pair<Timestamp,String>>> messages;
    Map<String,Timestamp> tim;

    public void Recievedmessages(String email, String message, String tmp){
        Intent i = new Intent(Chatlist.this,Chatlist.class);
        i.putExtra("remail",email);
        i.putExtra("msg",message);
        i.putExtra("tmp",tmp);
        i.putExtra("email",this.getIntent().getStringExtra("email"));
        i.putExtra("Password",this.getIntent().getStringExtra("Password"));
        startActivityForResult(i,0);
        finish();
    }

    private void addMessage(String email,String message,String tmp){
        try {
            FileOutputStream fout = openFileOutput("msgs",MODE_APPEND);
            PrintWriter pw = new PrintWriter(fout);
            pw.println(email);
            pw.println(message);
            pw.println(tmp);
            pw.close();
            fout.close();
            updateMessageUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMessageUI() {
        messages = new HashMap<>();
        tim = new HashMap<>();
        try{
            FileInputStream fin = this.openFileInput("msgs");
            InputStreamReader in = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(in);
            String email;
            while((email = br.readLine())!=null){

                String msg = br.readLine();
                String c = br.readLine();
                //Log.println(Log.ERROR,"MSG",email);
                c = c.substring(0,10)+" "+c.substring(11,19);
                Timestamp tmp = new Timestamp(0);
                try{
                    tmp = Timestamp.valueOf(c);}catch(Exception e){
                    Log.println(Log.ERROR,"ERROR",c);
                }

                if (!messages.containsKey(email)) {
                    messages.put(email, new ArrayList<>());
                }
                messages.get(email).add(new Pair<>(tmp,msg));
                tim.put(email,tmp);
            }
            br.close();
            in.close();
            fin.close();
            List<Map.Entry<String,ArrayList<Pair<Timestamp,String>>>> list = new LinkedList<>(messages.entrySet());
            Collections.sort(list, (o1, o2) -> -tim.get(o1.getKey()).compareTo(tim.get(o2.getKey())));
            chats.removeAllViews();
            for(Map.Entry<String,ArrayList<Pair<Timestamp,String>>> e:list){
                TextView txt = new TextView(this);
                txt.setText(e.getKey());
                txt.setOnClickListener(v -> {
                    OpenChat(e.getKey());
                });
                chats.addView(txt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void OpenChat(String key) {
        Intent in = new Intent(Chatlist.this,Chat.class);
        startActivityForResult(in,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        name = findViewById(R.id.Name1);
        chats = findViewById(R.id.chatlist);
        if(this.getIntent().getStringExtra("remail")!=null){
            addMessage(this.getIntent().getStringExtra("remail"),this.getIntent().getStringExtra("msg"),this.getIntent().getStringExtra("tmp"));
        }
        updateMessageUI();
        ServerConnecter chatsync = new ServerConnecter(ServerConnecter.Operations.CHATS,this.getIntent().getStringExtra("email"),this.getIntent().getStringExtra("Password"),this);
        chatsync.start();

    }
}