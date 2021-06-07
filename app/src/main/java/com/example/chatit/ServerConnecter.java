package com.example.chatit;

import android.os.Build;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.util.JsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

public class ServerConnecter extends Thread {

    public enum Operations {LOGIN, REGISTER, MESSAGE, CHATS,CHANGENAME}


    private URL url;
    private String email;
    private String Password;
    AppCompatActivity activity;
    private Operations op;
    private Queue<Pair<String,String>> queue;

    public ServerConnecter(Operations op, String email, String Password, AppCompatActivity activity) {
        if (op == Operations.LOGIN) {
            try {
                url = new URL("https://chatit-server.herokuapp.com/login");
                this.email = email;
                this.Password = Password;
                this.activity = activity;
                this.op = op;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (op == Operations.REGISTER) {

            try {
                url = new URL("https://chatit-server.herokuapp.com/register");
                this.email = email;
                this.Password = Password;
                this.activity = activity;
                this.op = op;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if (op == Operations.CHATS) {
            try {
                url = new URL("https://chatit-server.herokuapp.com/chats");
                this.email = email;
                this.Password = Password;
                this.activity = activity;
                this.op = op;
                queue = new LinkedList<>();
                queue.add(new Pair<>(email,Password));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (op == Operations.LOGIN) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + email + "&Password=" + Password;
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    Log.d("TAG", connection.getResponseMessage());
                    InputStream in = connection.getInputStream();
                    String response = "" + (char) in.read();
                    if (in.available() > 0) {
                        response += (char) in.read();
                    }
                    if (!response.equals("-1")) ((MainActivity) activity).UpdateUI(email,Password);
                } else {
                    Log.d("TAG", connection.getResponseMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (op == Operations.REGISTER) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Username=janedoe&Email=" + email + "&Password=" + Password;
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    Log.d("TAG", connection.getResponseMessage());
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String response = br.readLine();
                    if (!response.equals("-1")) ((MainActivity) activity).askName();

                } else {
                    Log.d("TAG", connection.getResponseMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (op == Operations.CHATS) {
            try {
                while(!queue.isEmpty()){
                    queue.remove();
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String data = "Email=" + email + "&Password=" + Password;
                    byte[] out = data.getBytes(StandardCharsets.UTF_8);
                    OutputStream stream = connection.getOutputStream();
                    stream.write(out);
                    if (connection.getResponseCode() == 200) {
                        //Log.d("TAG", connection.getResponseMessage());
                        InputStream in = connection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String response = br.readLine();
                        //Log.println(Log.ERROR,"Error",response);
                        JSONArray res = new JSONArray(response);
                        int msags = res.length();
                        for(int i=0;i<msags;i++){
                            JSONObject obj = res.getJSONObject(0);
                            String email = obj.getJSONObject("sender").getString("email");
                            String message = obj.getJSONObject("message").getString("message");
                            String tmp = obj.getString("timeStamp");
                            ((Chatlist)activity).Recievedmessages(email,message,tmp);
                        }
                    }else{

                    }
                    if(!activity.isDestroyed())queue.add(new Pair<>("",""));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}