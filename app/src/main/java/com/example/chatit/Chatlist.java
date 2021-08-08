package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Activity showing users with recent chats
 */
public class Chatlist extends AppCompatActivity {

    private LinearLayout chats_display;
    private Chats chats;

    private final BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            chats = ServerConnect.getChats();
            updateUI();
        }
    };

    private void updateUI() {
        List<Map.Entry<String, Pair<Timestamp, String>>> list = new ArrayList<>(chats.display_list.entrySet());
        Collections.sort(list, (o1, o2) -> -o1.getValue().first.compareTo(o2.getValue().first));
        chats_display.removeAllViews();
        for (Map.Entry<String, Pair<Timestamp, String>> value : list) {
            TextView txt = new TextView(this);
            String msg = value.getValue().second;
            msg = msg.replaceAll("\n", " ");
            if (msg.length() > 10) msg = msg.substring(0, 10) + "...";
            txt.setText(MessageFormat.format("{0}\n{1}: {2}", chats.usernames.get(value.getKey()), value.getValue().first, msg));
            txt.setPadding(10, 10, 10, 10);
            txt.setOnClickListener(v -> {
                Intent intent = new Intent(this, Chat.class);
                intent.putExtra("email", this.getIntent().getStringExtra("email"));
                intent.putExtra("password", this.getIntent().getStringExtra("password"));
                intent.putExtra("remail", value.getKey());
                intent.putExtra("uname", chats.usernames.get(value.getKey()));
                startActivityForResult(intent, 0);
            });
            chats_display.addView(txt);
            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    5
            ));
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));

            chats_display.addView(v);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        TextView name = findViewById(R.id.Name1);
        name.setText(this.getIntent().getStringExtra("email"));
        chats_display = findViewById(R.id.chatlist);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, Select_User.class);
            intent.putExtra("email", this.getIntent().getStringExtra("email"));
            intent.putExtra("password", this.getIntent().getStringExtra("password"));
            startActivityForResult(intent, 0);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("com.example.chatit.CHATSYNC"));
        Intent loadChat = new Intent(this, ServerConnect.class);
        loadChat.setAction("LOADCHATOFFLINE");
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, loadChat);
        Intent chatsync = new Intent(this, ServerConnect.class);
        chatsync.setAction("CHATS");
        chatsync.putExtra("email", this.getIntent().getStringExtra("email"));
        chatsync.putExtra("password", MainActivity.HashPassword(this.getIntent().getStringExtra("password")));
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chatsync);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }
}