package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Start page (User login and Register).
 */
public class MainActivity extends AppCompatActivity {

    Button Login;
    Button Register;

    private final BroadcastReceiver serverResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int res = intent.getIntExtra("valid",0);
            if (res == 1) {
                openChatList(intent.getStringExtra("email"), intent.getStringExtra("password"));
            }
            else if (res == 2) {
                askName(intent.getStringExtra("email"), intent.getStringExtra("password"));
            }
            else if (res == 3) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.mainveiw), "Email already exists", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.mainveiw), "Email or Password incorrect", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    };

    public static String HashPassword(String pass) {
        String hash = pass;
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            hash = Base64.encodeToString(m.digest(pass.getBytes(StandardCharsets.UTF_8)),Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    private void usrLogin(String email, String password) {
        Intent checkPass = new Intent(this, ServerConnect.class);
        checkPass.setAction("LOGIN");
        checkPass.putExtra("email", email);
        checkPass.putExtra("password", HashPassword(password));
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair pair = gen.generateKeyPair();
            PrivateKey privatekey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            FileOutputStream fout = openFileOutput("pkey", MODE_PRIVATE);
            fout.write(privatekey.getEncoded());
            fout.close();
            checkPass.putExtra("pkey", Base64.encodeToString(publicKey.getEncoded(),Base64.NO_WRAP));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, checkPass);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.mainveiw), "Logging in", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void createAccount(String email, String password) {
        Intent regUser = new Intent(this, ServerConnect.class);
        regUser.setAction("REGISTER");
        regUser.putExtra("email", email);
        regUser.putExtra("password", HashPassword(password));
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair pair = gen.generateKeyPair();
            PrivateKey privatekey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            FileOutputStream fout = openFileOutput("pkey", MODE_PRIVATE);
            fout.write(privatekey.getEncoded());
            fout.close();
            regUser.putExtra("pkey", Base64.encodeToString(publicKey.getEncoded(),Base64.NO_WRAP));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, regUser);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.mainveiw), "Creating User", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void openChatList(String email, String password) {
        Intent openchatlist = new Intent(MainActivity.this, Chatlist.class);
        openchatlist.putExtra("email", email);
        openchatlist.putExtra("password", password);
        startActivityForResult(openchatlist, 0);
        finish();
    }

    public void askName(String email, String password) {
        Intent askname = new Intent(MainActivity.this, Ask_Name.class);
        askname.putExtra("email", email);
        askname.putExtra("password", password);
        startActivityForResult(askname, 0);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(serverResponse,new IntentFilter("com.exmaple.chatit.MAINACTIVITYRESPONSE"));
        try {
            FileInputStream fin = openFileInput("usr");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email = br.readLine();
            String Password = br.readLine();
            if (email != null && Password != null) {
                openChatList(email, Password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Login = findViewById(R.id.Login);
        Login.setOnClickListener(v -> {
            String Email = ((EditText) findViewById(R.id.Email)).getText().toString().trim();
            String Password = ((EditText) findViewById(R.id.Password)).getText().toString().trim();
            usrLogin(Email, Password);
        });
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(v -> {
            String Email = ((EditText) findViewById(R.id.Email)).getText().toString().trim();
            String Password = ((EditText) findViewById(R.id.Password)).getText().toString().trim();
            createAccount(Email, Password);
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serverResponse);
        super.onDestroy();
    }
}