package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Activity to show user chat to the ... user.
 */
public class Chat extends AppCompatActivity {

    private ArrayList<Pair<Pair<Timestamp, String>, Boolean>> chats;
    private final BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((EditText) findViewById(R.id.chatbox)).setText("");
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView veiw = findViewById(R.id.textView4);
        veiw.setText(this.getIntent().getStringExtra("uname"));
        veiw.setOnClickListener(v -> {
            Intent openusrinfo = new Intent(Chat.this, User_info.class);
            openusrinfo.putExtra("remail", this.getIntent().getStringExtra("remail"));
            openusrinfo.putExtra("uname", this.getIntent().getStringExtra("uname"));
            startActivityForResult(openusrinfo, 0);
        });
        EditText chatbox = findViewById(R.id.chatbox);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(v -> {
            String message = chatbox.getText().toString();
            if (!message.equals("")) {
                Intent i = new Intent(this, ServerConnect.class);
                i.setAction("MESSAGE");
                i.putExtra("email", this.getIntent().getStringExtra("email"));
                i.putExtra("password", this.getIntent().getStringExtra("password"));
                i.putExtra("remail", this.getIntent().getStringExtra("remail"));
                i.putExtra("uname", this.getIntent().getStringExtra("uname"));
                i.putExtra("msg", message);
                ServerConnect.enqueueWork(this, ServerConnect.class, 1000, i);
            }
        });
        updateUI();
        Intent chatsync = new Intent(this, ServerConnect.class);
        chatsync.setAction("CHATS");
        chatsync.putExtra("email", this.getIntent().getStringExtra("email"));
        chatsync.putExtra("password", this.getIntent().getStringExtra("password"));
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chatsync);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("com.example.chatit.CHATSYNC"));
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("com.example.chatit.CHATSYNC"));
        super.onResume();
    }

    private void updateUI() {
        fillChats();
        Collections.sort(chats, (o1, o2) -> o1.first.first.compareTo(o2.first.first));
        LinearLayout chat_display = findViewById(R.id.chatv);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        findViewById(R.id.chat).setBackgroundColor(Color.WHITE);
        chat_display.removeAllViews();
        for (Pair<Pair<Timestamp, String>, Boolean> chat : chats) {
            TextView txt = new TextView(this);
            txt.setText(String.format("%s\n%s", chat.first.second, chat.first.first.toString().substring(11, 16)));
            txt.setTextColor(Color.BLACK);
            txt.setPadding(10, 10, 10, 10);
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(16);
            shape.setColor(Color.argb(255, 211, 247, 198));
            txt.setBackground(shape);
            txt.setLayoutParams(params);
            txt.setEllipsize(TextUtils.TruncateAt.END);
            chat_display.addView(txt);
            if (chat.second) ((LinearLayout.LayoutParams) txt.getLayoutParams()).gravity = Gravity.RIGHT;
            txt.requestLayout();
        }
        findViewById(R.id.chat).post(() -> findViewById(R.id.chat).scrollTo(0, findViewById(R.id.chatv).getBottom()));
    }

    private void fillChats() {
        chats = new ArrayList<>();
        ArrayList<Pair<Timestamp, String>> userChats = ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"));
        if (userChats != null) {
            for (Pair<Timestamp, String> m : userChats) {
                chats.add(new Pair<>(new Pair<>(m.first, m.second), false));
            }
        }
        userChats = ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"));
        if (userChats != null) {
            for (Pair<Timestamp, String> m : userChats) {
                chats.add(new Pair<>(new Pair<>(m.first, m.second), true));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }
}