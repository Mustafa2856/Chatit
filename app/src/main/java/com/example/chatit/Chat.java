package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    private ArrayList<Pair<Timestamp,String>> chats;
    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView veiw = findViewById(R.id.textView4);
        veiw.setText(this.getIntent().getStringExtra("uname"));
        chats =(ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"))==null)?null: (ArrayList<Pair<Timestamp, String>>) ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail")).clone();
        if(chats == null)chats = new ArrayList<>();
        try{
            chats.addAll(Objects.requireNonNull(ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"))));
        }catch (NullPointerException e){

        }
        EditText chatbox = findViewById(R.id.chatbox);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(v -> {
            String message = chatbox.getText().toString();
            Intent i = new Intent(this,ServerConnect.class);
            i.setAction("MESSAGE");
            i.putExtra("email",this.getIntent().getStringExtra("email"));
            i.putExtra("Password",this.getIntent().getStringExtra("Password"));
            i.putExtra("remail",this.getIntent().getStringExtra("remail"));
            i.putExtra("uname",this.getIntent().getStringExtra("uname"));
            i.putExtra("msg",message);
            ServerConnect.enqueueWork(this,ServerConnect.class,1000,i);
            Log.println(Log.ERROR,"this","is called??");
            //this.startService(i);
            //chats.add(new Pair<>(new Timestamp(System.currentTimeMillis()),message));
            //updateUI();
        });
        updateUI();
        Intent chatsync = new Intent(this,ServerConnect.class);
        chatsync.setAction("CHATS");
        chatsync.putExtra("email",this.getIntent().getStringExtra("email"));
        chatsync.putExtra("Password",this.getIntent().getStringExtra("Password"));
        ServerConnect.enqueueWork(this,ServerConnect.class,1000,chatsync);
        //this.startService(chatsync);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever,new IntentFilter("com.example.chatit.CHATSYNC"));
    }

    private void updateUI(){
        chats =(ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"))==null)?null: (ArrayList<Pair<Timestamp, String>>) ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail")).clone();
        if(chats == null)chats = new ArrayList<>();
        try{
            chats.addAll(Objects.requireNonNull(ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"))));
        }catch (NullPointerException e){

        }
        Collections.sort(chats, (o1, o2) -> o1.first.compareTo(o2.first));
        LinearLayout chat_display = findViewById(R.id.chatv);
        chat_display.removeAllViews();
        for(Pair<Timestamp,String> chat:chats){
            TextView txt = new TextView(this);
            txt.setText(chat.second+"\n\t"+chat.first.toString()+"\n");
            chat_display.addView(txt);
        }
        Log.println(Log.WARN,"MSGSCOUNT",""+chats.size());
        findViewById(R.id.chat).post(() -> findViewById(R.id.chat).scrollTo(0, findViewById(R.id.chat).getBottom()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }
}