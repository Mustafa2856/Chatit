package com.example.chatit;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.MessageFormat;

public class User_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        FloatingActionButton btn = findViewById(R.id.backbtn);
        btn.setOnClickListener(v -> finish());
        TextView uname = findViewById(R.id.usrname);
        uname.setText(this.getIntent().getStringExtra("uname"));
        TextView email = findViewById(R.id.emailusrinfo);
        email.setText(MessageFormat.format("Email: {0}", this.getIntent().getStringExtra("remail")));
    }
}