package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;


/**
 * Activity to search user in database to start new chat
 */
public class Select_User extends AppCompatActivity {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONArray arr = new JSONArray(intent.getStringExtra("list"));
                int size = arr.length();
                LinearLayout users = findViewById(R.id.users);
                users.removeAllViews();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String email = obj.getString("email");
                    String uname = obj.getString("uname");
                    TextView txt = new TextView(Select_User.this);
                    txt.setText(MessageFormat.format("{0}\n{1}", uname, email));
                    txt.setPadding(10, 10, 10, 10);
                    txt.setOnClickListener(v -> {
                        Intent in = new Intent(Select_User.this, Chat.class);
                        in.putExtra("email", Select_User.this.getIntent().getStringExtra("email"));
                        in.putExtra("Password", Select_User.this.getIntent().getStringExtra("Password"));
                        in.putExtra("remail", email);
                        in.putExtra("uname", uname);
                        startActivityForResult(in, 0);
                    });
                    users.addView(txt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__user);
        EditText searchbar = findViewById(R.id.editTextTextPersonName);
        Button sbutton = findViewById(R.id.button);
        sbutton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ServerConnect.class);
            intent.setAction("FINDUSER");
            intent.putExtra("uname", searchbar.getText().toString());
            intent.putExtra("email", this.getIntent().getStringExtra("email"));
            intent.putExtra("Password", this.getIntent().getStringExtra("Password"));
            ServerConnect.enqueueWork(this, ServerConnect.class, 1000, intent);
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.example.chatit.USRLIST"));
    }
}