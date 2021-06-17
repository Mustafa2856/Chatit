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

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {

    Button Login;
    Button Register;

    public static String HashPassword(String pass){
        String hash=pass;
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            hash = toHexString(m.digest(pass.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    private void Check_Password_with_db(String email, String password){
        //Thread con = new ServerConnecter(ServerConnecter.Operations.LOGIN,email,password,this);
        //con.start();
        Intent checkPass = new Intent(this,ServerConnect.class);
        checkPass.setAction("LOGIN");
        checkPass.putExtra("email",email);
        checkPass.putExtra("Password",HashPassword(password));
        checkPass.putExtra("op",password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.println(Log.ERROR,"INCS","whyyyyy");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                UpdateUI(intent.getStringExtra("email"),intent.getStringExtra("op"));
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
        regUser.putExtra("Password",HashPassword(password));
        regUser.putExtra("op",password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.println(Log.ERROR,"INCS","whyyyyy");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                askName(intent.getStringExtra("email"),intent.getStringExtra("op"));
            }
        },new IntentFilter("com.exmaple.chatit.OPENASKNAME"));
        ServerConnect.enqueueWork(this,ServerConnect.class,1000,regUser);
        //this.startService(regUser);
    }

    public void UpdateUI(String email,String Password){
            Intent openchatlist = new Intent(MainActivity.this,Chatlist.class);
            openchatlist.putExtra("email",email);
            openchatlist.putExtra("Password",Password);
        Log.e("?","???????");
            startActivityForResult(openchatlist,0);
            finish();
    }

    public void askName(String email,String Password){
        Intent askname = new Intent(MainActivity.this,Ask_Name.class);
        askname.putExtra("email",email);
        askname.putExtra("Password",Password);
        Log.e("?","???????");
        startActivityForResult(askname,0);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            FileInputStream fin = openFileInput("usr");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email = br.readLine();
            String Password = br.readLine();
            if(email!=null && Password!=null){
                //Password = HashPassword(Password);
                //Check_Password_with_db(email,Password);
                UpdateUI(email,Password);
            }
        }catch(IOException e){

        }
        Login = findViewById(R.id.Login);
        Login.setOnClickListener(v -> {
            String Email = ((EditText)findViewById(R.id.Email)).getText().toString();
            String Password = ((EditText)findViewById(R.id.Password)).getText().toString();
            Check_Password_with_db(Email,Password);
        });
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(v -> {
            String Email = ((EditText)findViewById(R.id.Email)).getText().toString();
            String Password = ((EditText)findViewById(R.id.Password)).getText().toString();
            //Password = HashPassword(Password);
            createAccount(Email,Password);
        });
    }
}