package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

    public static String HashPassword(String pass) {
        String hash = pass;
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            hash = toHexString(m.digest(pass.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void Check_Password_with_db(String email, String password) {
        Intent checkPass = new Intent(this, ServerConnect.class);
        checkPass.setAction("LOGIN");
        checkPass.putExtra("email", email);
        checkPass.putExtra("Password", HashPassword(password));
        checkPass.putExtra("op", password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                UpdateUI(intent.getStringExtra("email"), intent.getStringExtra("op"));
            }
        }, new IntentFilter("com.exmaple.chatit.OPENCHAT"));
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, checkPass);

    }

    private void createAccount(String email, String password) {
        Intent regUser = new Intent(this, ServerConnect.class);
        regUser.setAction("REGISTER");
        regUser.putExtra("email", email);
        regUser.putExtra("Password", HashPassword(password));
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair pair = gen.generateKeyPair();
            PrivateKey privatekey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            FileOutputStream fout = openFileOutput("pkey", MODE_PRIVATE);
            fout.write(privatekey.getEncoded());
            fout.close();
            regUser.putExtra("pkey", toHexString(publicKey.getEncoded()));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        regUser.putExtra("op", password);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
                askName(intent.getStringExtra("email"), intent.getStringExtra("op"));
            }
        }, new IntentFilter("com.exmaple.chatit.OPENASKNAME"));
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, regUser);
    }

    public void UpdateUI(String email, String Password) {
        Intent openchatlist = new Intent(MainActivity.this, Chatlist.class);
        openchatlist.putExtra("email", email);
        openchatlist.putExtra("Password", Password);
        startActivityForResult(openchatlist, 0);
        finish();
    }

    public void askName(String email, String Password) {
        Intent askname = new Intent(MainActivity.this, Ask_Name.class);
        askname.putExtra("email", email);
        askname.putExtra("Password", Password);
        Log.e("?", "???????");
        startActivityForResult(askname, 0);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileInputStream fin = openFileInput("usr");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email = br.readLine();
            String Password = br.readLine();
            if (email != null && Password != null) {
                UpdateUI(email, Password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Login = findViewById(R.id.Login);
        Login.setOnClickListener(v -> {
            String Email = ((EditText) findViewById(R.id.Email)).getText().toString().trim();
            String Password = ((EditText) findViewById(R.id.Password)).getText().toString().trim();
            Check_Password_with_db(Email, Password);
        });
        Register = findViewById(R.id.Register);
        Register.setOnClickListener(v -> {
            String Email = ((EditText) findViewById(R.id.Email)).getText().toString().trim();
            String Password = ((EditText) findViewById(R.id.Password)).getText().toString().trim();
            createAccount(Email, Password);
        });
    }
}