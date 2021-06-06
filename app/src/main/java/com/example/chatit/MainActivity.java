package com.example.chatit;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    Button Login;
    Button Register;

    String HashPassword(String pass){
        return pass;
    }

    private void Check_Password_with_db(String email, String password){
        Thread con = new ServerConnecter(ServerConnecter.Operations.LOGIN,email,password,this);
        con.start();
    }

    private void createAccount(String email, String password) {
        Thread con = new ServerConnecter(ServerConnecter.Operations.REGISTER,email,password,this);
        con.start();
    }

    public void UpdateUI(String email,String Password){
            Intent openchatlist = new Intent(MainActivity.this,Chatlist.class);
            openchatlist.putExtra("email",email);
            openchatlist.putExtra("Password",Password);
            startActivityForResult(openchatlist,0);
    }

    public void askName(){
        Intent askname = new Intent(MainActivity.this,Ask_Name.class);
        startActivityForResult(askname,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login = findViewById(R.id.Login);
        Login.setOnClickListener(v -> {
            String Email = ((EditText)findViewById(R.id.Email)).getText().toString();
            String Password = ((EditText)findViewById(R.id.Password)).getText().toString();
            Password = HashPassword(Password);
            Check_Password_with_db(Email,Password);
        });
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(v -> {
            String Email = ((EditText)findViewById(R.id.Email)).getText().toString();
            String Password = ((EditText)findViewById(R.id.Password)).getText().toString();
            Password = HashPassword(Password);
            createAccount(Email,Password);
        });
    }
}