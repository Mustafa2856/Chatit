package com.example.chatit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class ServerConnect extends JobIntentService {

    static final int JOB_ID = 1000;
    public enum Operations {LOGIN,REGISTER,CHANGENAME,MESSAGE,CHATS,LOADCHATOFFLINE}
    public static Chats chats;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if(chats==null)chats = new Chats();
        Operations op;
        try{
           op = Operations.valueOf(intent.getAction());
        }catch(IllegalArgumentException e){
            Log.println(Log.ERROR,"NON ASSIGNED INTENT:-",e.getMessage());
            return;
        }
        //Log.println(Log.ERROR,"INsC","something wrong i can feel it");
        if(op == Operations.LOGIN){
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/login");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("Password");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                //Log.println(Log.ERROR,"TAG", connection.getResponseCode()+"");
                if (connection.getResponseCode() == 200) {
                    //Log.println(Log.ERROR,"TAG", "connection.getResponseMessage()");
                    InputStream in = connection.getInputStream();
                    String response = "";
                    while (in.available() > 0) {
                        response += (char) in.read();
                    }
                    if (!response.equals("-1")){
                        Intent openchatlist = new Intent();
                        openchatlist.setAction("com.exmaple.chatit.OPENCHAT");
                        openchatlist.putExtra("email",intent.getStringExtra("email"));
                        openchatlist.putExtra("Password",intent.getStringExtra("Password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openchatlist);
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr",MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("Password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
                        //Log.println(Log.ERROR,"TAG", "1");
                    }else{
                        //Log.println(Log.ERROR,"TAG", "2");
                    }
                } else {
                    //Log.println(Log.ERROR,"TAG", "3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(op == Operations.REGISTER){
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/register");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Username=janedoe&Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("Password");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                //Log.println(Log.ERROR,"TAG", connection.getResponseCode()+"");g
                if (connection.getResponseCode() == 200) {
                    //Log.println(Log.ERROR,"TAG", "connection.getResponseMessage()");
                    InputStream in = connection.getInputStream();
                    String response = "";
                    while (in.available() > 0) {
                        response += (char) in.read();
                    }
                    if (!response.equals("-1")){
                        Intent openan = new Intent();
                        openan.setAction("com.exmaple.chatit.OPENASKNAME");
                        openan.putExtra("email",intent.getStringExtra("email"));
                        openan.putExtra("Password",intent.getStringExtra("Password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openan);
                        //Log.println(Log.ERROR,"TAG", "1");
                    }else{
                        //Log.println(Log.ERROR,"TAG", "2");
                    }
                } else {
                    //Log.println(Log.ERROR,"TAG", "3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(op == Operations.CHANGENAME){
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/changename");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Username="+intent.getStringExtra("uname")+"&Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("Password");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                //Log.println(Log.ERROR,"TAG", connection.getResponseCode()+"");g
                if (connection.getResponseCode() == 200) {
                    //Log.println(Log.ERROR,"TAG", "connection.getResponseMessage()");
                    InputStream in = connection.getInputStream();
                    String response = "";
                    while (in.available() > 0) {
                        response += (char) in.read();
                    }
                    if (!response.equals("-1")){
                        Intent openan = new Intent();
                        openan.setAction("com.exmaple.chatit.OPENCHATLIST");
                        openan.putExtra("email",intent.getStringExtra("email"));
                        openan.putExtra("Password",intent.getStringExtra("Password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openan);
                        //Log.println(Log.ERROR,"TAG", "1");
                    }else{
                        //Log.println(Log.ERROR,"TAG", "2");
                    }
                } else {
                    //Log.println(Log.ERROR,"TAG", "3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(op == Operations.CHATS){
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/chats");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("Password");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                //Log.println(Log.ERROR,"TAG", connection.getResponseCode()+"");
                if (connection.getResponseCode() == 200) {
                    //Log.println(Log.ERROR,"TAG", "connection.getResponseMessage()");
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    //Log.println(Log.ERROR,"TAG", response);
                    if(response!=null){
                        try{
                            JSONArray res = new JSONArray(response);
                            int msgs = res.length();
                            int mode = MODE_PRIVATE;
                            if(checkUserStored(intent.getStringExtra("email")))mode |= MODE_APPEND;
                            else{
                                chats = new Chats();
                                FileOutputStream fout = openFileOutput("usr",MODE_PRIVATE);
                                fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                                fout.write("\n".getBytes(StandardCharsets.UTF_8));
                                fout.write(intent.getStringExtra("Password").getBytes(StandardCharsets.UTF_8));
                                fout.close();
                            }
                            //Log.println(Log.ERROR,"RESPONSE REC","response.toString()");
                            FileOutputStream fout = openFileOutput("msgs",mode);
                            PrintWriter pw = new PrintWriter(fout);
                            for(int i=0;i<msgs;i++){
                                JSONObject obj = res.getJSONObject(i);
                                String email = obj.getJSONObject("sender").getString("email");
                                String uname = obj.getJSONObject("sender").getString("uname");
                                String message = obj.getJSONObject("message").getString("message");
                                //Log.println(Log.ERROR,"tmp",obj.getString("timeStamp"));
                                Timestamp tmp = new Timestamp(Timestamp.valueOf(obj.getString("timeStamp").substring(0,10) + " " + obj.getString("timeStamp").substring(11,19)).getTime() + TimeZone.getDefault().getRawOffset());
                                //Timestamp tmp = Timestamp.valueOf(obj.getString("timeStamp"));
                                chats.addMessage(uname,email,message,tmp);
                                pw.println(email);
                                pw.println(uname);
                                String msg = message;
                                msg = msg.replaceAll("[\r\n]","\u259f");
                                Log.e("MSG",msg);
                                pw.println(msg);
                                pw.println(tmp);
                            }
                            pw.close();
                            fout.close();
                            if(msgs>0){
                                Intent notifyUI = new Intent();
                                notifyUI.setAction("com.example.chatit.CHATSYNC");
                                LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);

                                //this.startService(chats);
                            }
                            Intent chats = new Intent(this,ServerConnect.class);
                            chats.setAction("CHATS");
                            chats.putExtra("email",intent.getStringExtra("email"));
                            chats.putExtra("Password",intent.getStringExtra("Password"));
                            ServerConnect.enqueueWork(this,ServerConnect.class,1000,chats);
                        }catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.println(Log.ERROR,"TAG", "3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(op == Operations.LOADCHATOFFLINE){
            LoadChats();
            Intent notifyUI = new Intent();
            notifyUI.setAction("com.example.chatit.CHATSYNC");
            LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
            Intent chats = new Intent(this,ServerConnect.class);
            chats.setAction("CHATS");
            chats.putExtra("email",intent.getStringExtra("email"));
            chats.putExtra("Password",intent.getStringExtra("Password"));
            //ServerConnect.enqueueWork(this,ServerConnect.class,1000,chats);
            //this.startService()
        }
        else if(op == Operations.MESSAGE){
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/message");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("Password") + "&ReceiverEmail=" + intent.getStringExtra("remail") + "&message=" + intent.getStringExtra("msg");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                //Log.println(Log.ERROR,"res","response");
                //Log.println(Log.ERROR,"TAG", connection.getResponseCode()+"");
                if (connection.getResponseCode() == 200) {
                    //Log.println(Log.ERROR,"TAG", "connection.getResponseMessage()");
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    //Log.println(Log.ERROR,"res",response);
                    if(!response.equals("2"))return;
                    Timestamp tmp = new Timestamp(System.currentTimeMillis());
                    chats.sendMessage(intent.getStringExtra("uname"),intent.getStringExtra("remail"),intent.getStringExtra("msg"),tmp);
                    int mode = MODE_PRIVATE;
                    if(checkUserStored(intent.getStringExtra("email")))mode |= MODE_APPEND;
                    else{
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr",MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("Password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
                    }
                    FileOutputStream fout = openFileOutput("sm",mode);
                    PrintWriter pw = new PrintWriter(fout);
                    pw.println(intent.getStringExtra("remail"));
                    pw.println(intent.getStringExtra("uname"));
                    String msg = intent.getStringExtra("msg");
                    msg = msg.replaceAll("[\r\n]","\u259f");
                    pw.println(msg);
                    pw.println(tmp);
                    pw.close();
                    fout.close();
                    Intent notifyUI = new Intent();
                    notifyUI.setAction("com.example.chatit.CHATSYNC");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
                } else {
                    //Log.println(Log.ERROR,"TAG", "3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void LoadChats() {
        try {
            FileInputStream fin = openFileInput("msgs");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email,uname,message,tmp;
            while((email=br.readLine())!=null){
                uname = br.readLine();
                message = br.readLine();
                message = message.replaceAll("\u259f","\r\n");
                tmp = br.readLine();
                Log.e("tmp",tmp);
                chats.addMessage(uname,email,message,Timestamp.valueOf(tmp.substring(0,10) + " " + tmp.substring(11,19)));
            }
            br.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fin = openFileInput("sm");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email,uname,message,tmp;
            while((email=br.readLine())!=null){
                uname = br.readLine();
                message = br.readLine();
                message = message.replaceAll("\u259f","\r\n");
                tmp = br.readLine();
                Log.e("tmp",tmp);
                chats.sendMessage(uname,email,message,Timestamp.valueOf(tmp.substring(0,10) + " " + tmp.substring(11,19)));
            }
            br.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Chats getChats(){
        return chats;
    }

    private boolean checkUserStored(String email){
        String chkmail = null;
        try {
            chkmail = new BufferedReader(new InputStreamReader(openFileInput("usr"))).readLine();
        } catch (IOException e) {
            return false;
        }
        if(chkmail==null)return false;
        return chkmail.equals(email);
    }
}