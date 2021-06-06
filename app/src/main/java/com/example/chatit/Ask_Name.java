package com.example.chatit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class Ask_Name extends AppCompatActivity {

    private void setUname(String name){
        String email = this.getIntent().getStringExtra("email");
        Thread con = new ServerConnecter(ServerConnecter.Operations.CHANGENAME,email,name,this);
        con.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_name);
        Button next = findViewById(R.id.button2);
        next.setOnClickListener(v -> {
            String name = ((EditText)findViewById(R.id.uname)).getText().toString();
            setUname(name);
            Intent openmainactivity = new Intent(Ask_Name.this,Chatlist.class);
            openmainactivity.putExtra("email",this.getIntent().getStringExtra("email"));
            openmainactivity.putExtra("Password",this.getIntent().getStringExtra("Password"));
            startActivityForResult(openmainactivity,0);
        });
    }
}