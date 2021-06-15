package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    private ArrayList<Pair<Pair<Timestamp,String>,Boolean>> chats;
    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((EditText)findViewById(R.id.chatbox)).setText("");updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView veiw = findViewById(R.id.textView4);

        veiw.setText(this.getIntent().getStringExtra("uname"));
        /*chats =(ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"))==null)?null: (ArrayList<Pair<Timestamp, String>>) ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail")).clone();
        if(chats == null)chats = new ArrayList<>();
        try{
            chats.addAll(Objects.requireNonNull(ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"))));
        }catch (NullPointerException e){

        }*/
        //fillChats();
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
            //Log.println(Log.ERROR,"this","is called??");
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
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever,new IntentFilter("com.example.chatit.CHATSYNC"));
        super.onResume();
    }

    private void updateUI(){
        /*chats =(ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"))==null)?null: (ArrayList<Pair<Timestamp, String>>) ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail")).clone();
        if(chats == null)chats = new ArrayList<>();
        try{
            chats.addAll(Objects.requireNonNull(ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"))));
        }catch (NullPointerException e){

        }*/
        fillChats();
        Collections.sort(chats, (o1, o2) -> o1.first.first.compareTo(o2.first.first));
        LinearLayout chat_display = findViewById(R.id.chatv);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        //params.gravity()
        //chat_display.setLayoutParams(params);
        //chat_display.setPadding(5,5,5,5);
        findViewById(R.id.chat).setBackgroundColor(Color.WHITE);
        chat_display.removeAllViews();
        for(Pair<Pair<Timestamp,String>,Boolean> chat:chats){
            TextView txt = new TextView(this);
            txt.setText(chat.first.second+"\n"+chat.first.first.toString().substring(11,16));
            txt.setBackgroundColor(Color.argb(255,211,247,198));
            txt.setTextColor(Color.BLACK);
            txt.setPadding(10,10,10,10);
            //Log.println(Log.ERROR,"Width",Resources.getSystem().getDisplayMetrics().widthPixels/2+"");
            //txt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            //txt.setWidth(Resources.getSystem().getDisplayMetrics().widthPixels/2);
            //if(chat.second)params.gravity = Gravity.RIGHT;
            txt.setLayoutParams(params);
            txt.setEllipsize(TextUtils.TruncateAt.END);
            chat_display.addView(txt);
            if(chat.second)((LinearLayout.LayoutParams)txt.getLayoutParams()).gravity=Gravity.RIGHT;
            txt.requestLayout();
        }
        //Log.println(Log.WARN,"MSGSCOUNT",""+chats.size());
        findViewById(R.id.chat).post(() -> findViewById(R.id.chat).scrollTo(0, findViewById(R.id.chat).getBottom()));
    }

    private void fillChats() {
        chats = new ArrayList<>();
        ArrayList<Pair<Timestamp, String>> c = ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"));
        if(c!=null){
        for(Pair<Timestamp, String> m:c){
            chats.add(new Pair<>(new Pair<>(m.first,m.second),false));
        }}
        c = ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"));
        if(c!=null){
        for(Pair<Timestamp, String> m:c){
            chats.add(new Pair<>(new Pair<>(m.first,m.second),true));
        }}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }
}