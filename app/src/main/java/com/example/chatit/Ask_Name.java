package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Ask_Name extends AppCompatActivity {

    private void setUname(String name){
        //Thread con = new ServerConnecter(ServerConnecter.Operations.CHANGENAME,email,name,this);
        //con.start();
        Intent chn = new Intent(this,ServerConnect.class);
        chn.setAction("CHANGENAME");
        chn.putExtra("email",this.getIntent().getStringExtra("email"));
        chn.putExtra("Password",this.getIntent().getStringExtra("Password"));
        chn.putExtra("uname",name);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(Ask_Name.this).unregisterReceiver(this);
                Intent openchatlist = new Intent(Ask_Name.this,Chatlist.class);
                openchatlist.putExtra("email",Ask_Name.this.getIntent().getStringExtra("email"));
                openchatlist.putExtra("Password",Ask_Name.this.getIntent().getStringExtra("Password"));
                startActivityForResult(openchatlist,0);
            }
        },new IntentFilter("com.exmaple.chatit.OPENCHATLIST"));
        ServerConnect.enqueueWork(this,ServerConnect.class,1000,chn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_name);
        Button next = findViewById(R.id.button2);
        next.setOnClickListener(v -> {
            String name = ((EditText)findViewById(R.id.uname)).getText().toString();
            setUname(name);
        });
    }
}