package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MainActivity extends AppCompatActivity {

    Button Login;
    Button Register;

    String HashPassword(String pass){
        return pass;
    }

    private void Check_Password_with_db(String email, String password){
        //Thread con = new ServerConnecter(ServerConnecter.Operations.LOGIN,email,password,this);
        //con.start();
        Intent checkPass = new Intent(this,ServerConnect.class);
        checkPass.setAction("LOGIN");
        checkPass.putExtra("email",email);
        checkPass.putExtra("Password",password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.println(Log.ERROR,"INCS","whyyyyy");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                UpdateUI(intent.getStringExtra("email"),intent.getStringExtra("Password"));
            }
        },new IntentFilter("com.exmaple.chatit.OPENCHAT"));
        ServerConnect.enqueueWork(this,ServerConnect.class,1000,checkPass);
        //this.startService(checkPass);

    }

    private void createAccount(String email, String password) {
        //Thread con = new ServerConnecter(ServerConnecter.Operations.REGISTER,email,password,this);
        //con.start();
        Intent regUser = new Intent(this,ServerConnect.class);
        regUser.setAction("REGISTER");
        regUser.putExtra("email",email);
        regUser.putExtra("Password",password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.println(Log.ERROR,"INCS","whyyyyy");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                askName(intent.getStringExtra("email"),intent.getStringExtra("Password"));
            }
        },new IntentFilter("com.exmaple.chatit.OPENASKNAME"));
        ServerConnect.enqueueWork(this,ServerConnect.class,1000,regUser);
    }

    public void UpdateUI(String email,String Password){
            Intent openchatlist = new Intent(MainActivity.this,Chatlist.class);
            openchatlist.putExtra("email",email);
            openchatlist.putExtra("Password",Password);
            startActivityForResult(openchatlist,0);
    }

    public void askName(String email,String Password){
        Intent askname = new Intent(MainActivity.this,Ask_Name.class);
        askname.putExtra("email",email);
        askname.putExtra("Password",Password);
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