package com.example.chatit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


public class Chatlist extends AppCompatActivity {

    TextView name;
    LinearLayout chats;

    public void Recievedmessages(String email,String message){
        Intent i = new Intent(Chatlist.this,Chatlist.class);
        i.putExtra("remail",email);
        i.putExtra("msg",message);
        startActivityForResult(i,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        name = findViewById(R.id.Name1);
        chats = findViewById(R.id.chatlist);
        if(this.getIntent().getStringExtra("remail")==null){
        ServerConnecter chatsync = new ServerConnecter(ServerConnecter.Operations.CHATS,this.getIntent().getStringExtra("email"),this.getIntent().getStringExtra("Password"),this);
        chatsync.start();}
        else{
            TextView txt = new TextView(this);
            txt.setText(String.format("%s: %s", this.getIntent().getStringExtra("remail"), this.getIntent().getStringExtra("msg")));
            chats.addView(txt);
        }
    }
}