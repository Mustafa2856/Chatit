package com.example.chatit;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import com.google.android.gms.common.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

/**
 * Service to perform all client-server operations.
 */
public class ServerConnect extends JobIntentService {

    static final int JOB_ID = 1000;
    public static Chats chats;
    private static PrivateKey privateKey;
    public enum Operations {LOGIN, REGISTER, CHANGENAME, MESSAGE, CHATS, LOADCHATOFFLINE, FINDUSER}

    public static Chats getChats() {
        return chats;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (privateKey == null) {
            try {
                FileInputStream fin = openFileInput("pkey");
                int nRead;
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[128];
                while ((nRead = fin.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                EncodedKeySpec privatekeyspec = new PKCS8EncodedKeySpec(buffer.toByteArray());
                privateKey = KeyFactory.getInstance("RSA").generatePrivate(privatekeyspec);

            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        if (chats == null) chats = new Chats();
        Operations op;
        try {
            op = Operations.valueOf(intent.getAction());
        } catch (IllegalArgumentException e) {
            Log.println(Log.ERROR, "NON ASSIGNED INTENT:-", e.getMessage());
            return;
        }
        if (op == Operations.LOGIN) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/setpkey");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                String data = "{\"Email\":\"" + intent.getStringExtra("email")+ "\",\"Password\":\"" + intent.getStringExtra("password")+ "\",\"PublicKey\":\"" + intent.getStringExtra("pkey") + "\"}";
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null && !response.equals("-1")) {
                        Intent Response = new Intent();
                        Response.setAction("com.exmaple.chatit.MAINACTIVITYRESPONSE");
                        Response.putExtra("email", intent.getStringExtra("email"));
                        Response.putExtra("password", intent.getStringExtra("password"));
                        Response.putExtra("valid",1);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Response);
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
                    }
                    else{
                        Intent Response = new Intent();
                        Response.setAction("com.exmaple.chatit.MAINACTIVITYRESPONSE");
                        Response.putExtra("email", intent.getStringExtra("email"));
                        Response.putExtra("password", intent.getStringExtra("password"));
                        Response.putExtra("valid",0);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (op == Operations.REGISTER) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/register");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                String data = "{\"Username\":\"janedoe\",\"Email\":\"" + intent.getStringExtra("email")
                        + "\",\"Password\":\"" + intent.getStringExtra("password")
                        + "\",\"PublicKey\":\"" + intent.getStringExtra("pkey") + "\"}";
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null && !response.equals("-1")) {
                        Intent Response = new Intent();
                        Response.setAction("com.exmaple.chatit.MAINACTIVITYRESPONSE");
                        Response.putExtra("email", intent.getStringExtra("email"));
                        Response.putExtra("password", intent.getStringExtra("password"));
                        Response.putExtra("valid",2);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Response);
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
                    }
                    else {
                        Intent Response = new Intent();
                        Response.setAction("com.exmaple.chatit.MAINACTIVITYRESPONSE");
                        Response.putExtra("email", intent.getStringExtra("email"));
                        Response.putExtra("password", intent.getStringExtra("password"));
                        Response.putExtra("valid",3);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Response);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (op == Operations.CHANGENAME) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/changename");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                String data = "{\"Username\":\"" + intent.getStringExtra("uname")
                        + "\",\"Email\":\"" + intent.getStringExtra("email")
                        + "\",\"Password\":\"" + intent.getStringExtra("password") + "\"}";
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null && !response.equals("-1")) {
                        Intent openan = new Intent();
                        openan.setAction("com.exmaple.chatit.OPENCHATLIST");
                        openan.putExtra("email", intent.getStringExtra("email"));
                        openan.putExtra("password", intent.getStringExtra("password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openan);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (op == Operations.CHATS) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/chats");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                String timestamp = chats.tmp.toString();
                String data = "{\"Email\":\"" + intent.getStringExtra("email")
                        + "\",\"Password\":\"" + intent.getStringExtra("password")
                        + "\",\"Timestamp\":\"" + timestamp + "\"}";
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    int nRead;
                    byte[] dat = new byte[16384];
                    while ((nRead = is.read(dat, 0, dat.length)) != -1) {
                        bout.write(dat, 0, nRead);
                    }
                    if(bout.size()>0){
                        dat = bout.toByteArray();
                        String email = null,uname = null,type = null;
                        byte[] keyBytes = new byte[0];
                        int index=0,i=0;
                        bout = new ByteArrayOutputStream();
                        for(i=0;i<dat.length;i++){
                            if(dat[i] == '-') {
                                if(index == 0){
                                    email = new String(Base64.decode(bout.toByteArray(),Base64.NO_WRAP));
                                    bout = new ByteArrayOutputStream();
                                }
                                else if(index == 1){
                                    uname = new String(Base64.decode(bout.toByteArray(),Base64.NO_WRAP));
                                    bout = new ByteArrayOutputStream();
                                }
                                else if(index == 2){
                                    timestamp = new String(Base64.decode(bout.toByteArray(),Base64.NO_WRAP));
                                    bout = new ByteArrayOutputStream();
                                }
                                else if(index == 3){
                                    type = new String(Base64.decode(bout.toByteArray(),Base64.NO_WRAP));
                                    bout = new ByteArrayOutputStream();

                                }
                                else{
                                    keyBytes = Base64.decode(bout.toByteArray(),Base64.NO_WRAP);
                                    break;
                                }
                                index ++;
                            }
                            else bout.write(dat[i]);
                        }
                        bout = new ByteArrayOutputStream();
                        bout.write(dat,i+1, dat.length - i-1);
                        byte[] msgBytes = bout.toByteArray();
                        Cipher decryptCipherRSA = Cipher.getInstance("RSA");
                        decryptCipherRSA.init(Cipher.DECRYPT_MODE,privateKey);
                        Cipher decryptCipherAES = Cipher.getInstance("AES");
                        keyBytes = decryptCipherRSA.doFinal(keyBytes);
                        SecretKeySpec keySpec = new SecretKeySpec(keyBytes,"AES");
                        decryptCipherAES.init(Cipher.DECRYPT_MODE,keySpec);
                        msgBytes = decryptCipherAES.doFinal(msgBytes);
                        String msg = Base64.encodeToString(msgBytes,Base64.NO_WRAP);
                        Timestamp tmp = Timestamp.valueOf(timestamp);
                        chats.addMessage(uname,email,new Message(Message.type.valueOf(type),msgBytes),tmp);
                        int mode = MODE_PRIVATE;
                        if (checkdbStored(intent.getStringExtra("email"))) mode |= MODE_APPEND;
                        else {
                            chats = new Chats();
                            FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                            fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                            fout.write("\n".getBytes(StandardCharsets.UTF_8));
                            fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                            fout.close();
                        }
                        //TODO : Use database
                        MsgDatabase db = null;
                        try {
                            db = Room.databaseBuilder(this, MsgDatabase.class, "msg.db")
                                    .createFromFile(getDatabasePath("msg.db")).build();
                            Messages mssg = new Messages();
                            mssg.email = email;
                            mssg.message = uname;
                            mssg.tmp = timestamp;
                            mssg.type = Message.type.valueOf(type);
                            mssg.message = msg;
                            mssg.Sent = false;
                            db.messagesDao().InsertAll(mssg);
                            db.close();
                        }catch(Exception e){
                            db = Room.databaseBuilder(this,MsgDatabase.class,"msg.db").build();
                            Messages mssg = new Messages();
                            mssg.email = email;
                            mssg.message = uname;
                            mssg.tmp = timestamp;
                            mssg.type = Message.type.valueOf(type);
                            mssg.message = msg;
                            mssg.Sent = false;
                            db.messagesDao().InsertAll(mssg);
                            db.close();
                        }

                        /*FileOutputStream fout = openFileOutput("msgs", mode);
                        PrintWriter pw = new PrintWriter(fout);
                        pw.println(email);
                        pw.println(uname);
                        pw.println(type);
                        pw.println(timestamp);
                        pw.println(msg);
                        */
                        Intent notifyUI = new Intent();
                        notifyUI.setAction("com.example.chatit.CHATSYNC");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
                    }
                    Intent chats = new Intent(this, ServerConnect.class);
                    chats.setAction("CHATS");
                    chats.putExtra("email", intent.getStringExtra("email"));
                    chats.putExtra("password", intent.getStringExtra("password"));
                    ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chats);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (op == Operations.LOADCHATOFFLINE) {
            LoadChats();
            Intent notifyUI = new Intent();
            notifyUI.setAction("com.example.chatit.CHATSYNC");
            LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
            Intent chatsync = new Intent(this, ServerConnect.class);
            chatsync.setAction("CHATS");
            chatsync.putExtra("email", intent.getStringExtra("email"));
            chatsync.putExtra("password", intent.getStringExtra("password"));
            ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chatsync);
        }
        else if (op == Operations.MESSAGE) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/getpkey");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("remail");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(response,Base64.NO_WRAP));
                    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                    url = new URL("https://chatit-server.herokuapp.com/message");
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "text/plain");
                    Cipher encryptCipherRSA = Cipher.getInstance("RSA");
                    encryptCipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
                    Cipher encryptCipherAES = Cipher.getInstance("AES");
                    KeyGenerator keygen = KeyGenerator.getInstance("AES");
                    keygen.init(256);
                    SecretKey skey = keygen.generateKey();
                    encryptCipherAES.init(Cipher.ENCRYPT_MODE,skey);
                    byte[] message = Base64.decode(intent.getStringExtra("msg"),Base64.NO_WRAP);
                    byte[] messageBytes = encryptCipherAES.doFinal(message);
                    byte[] key = skey.getEncoded();
                    key = encryptCipherRSA.doFinal(key);
                    data = Base64.encodeToString(key,Base64.NO_WRAP) + '-';
                    data = Base64.encodeToString(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP) + '-'
                            + Base64.encodeToString(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP) + '-'
                            + Base64.encodeToString(intent.getStringExtra("remail").getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP) + '-'
                            + Base64.encodeToString(intent.getStringExtra("type").getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP) + '-'
                            + data;
                    out = data.getBytes();
                    stream = connection.getOutputStream();
                    stream.write(out);
                    stream.write(messageBytes);
                    if (connection.getResponseCode() == 200) {
                        in = connection.getInputStream();
                        br = new BufferedReader(new InputStreamReader(in));
                        response = br.readLine();
                        if (response == null) return;
                        if (!response.equals("2")) return;
                        Timestamp tmp = new Timestamp(System.currentTimeMillis() - TimeZone.getDefault().getRawOffset());
                        chats.sendMessage(intent.getStringExtra("uname"),
                                intent.getStringExtra("remail"),
                                new Message(Message.type.valueOf(intent.getStringExtra("type")),Base64.decode(intent.getStringExtra("msg"),Base64.NO_WRAP)),
                                tmp);
                        int mode = MODE_PRIVATE;
                        if (checkdbStored(intent.getStringExtra("email"))) mode |= MODE_APPEND;
                        else {
                            chats = new Chats();
                            FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                            fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                            fout.write("\n".getBytes(StandardCharsets.UTF_8));
                            fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                            fout.close();
                        }
                        MsgDatabase db = null;
                        try {
                            db = Room.databaseBuilder(this, MsgDatabase.class, "msg.db")
                                    .createFromFile(getDatabasePath("msg.db")).build();
                            Messages msg = new Messages();
                            msg.email = intent.getStringExtra("remail");
                            msg.message = intent.getStringExtra("uname");
                            msg.tmp = tmp.toString();
                            msg.type = Message.type.valueOf(intent.getStringExtra("type"));
                            msg.message = intent.getStringExtra("msg");
                            msg.Sent = true;
                            db.messagesDao().InsertAll(msg);
                            db.close();
                        }catch(Exception e){
                            db = Room.databaseBuilder(this,MsgDatabase.class,"msg.db").build();
                            Messages msg = new Messages();
                            msg.email = intent.getStringExtra("remail");
                            msg.message = intent.getStringExtra("uname");
                            msg.tmp = tmp.toString();
                            msg.type = Message.type.valueOf(intent.getStringExtra("type"));
                            msg.message = intent.getStringExtra("msg");
                            msg.Sent = true;
                            db.messagesDao().InsertAll(msg);
                            db.close();
                        }

                        Intent notifyUI = new Intent();
                        notifyUI.setAction("com.example.chatit.CHATSYNC");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (op == Operations.FINDUSER) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/finduser");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("uname");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null) {
                        Intent usrlist = new Intent();
                        usrlist.setAction("com.example.chatit.USRLIST");
                        usrlist.putExtra("list", response);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(usrlist);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void LoadChats() {
        try {
            /*
            FileInputStream fin = openFileInput("msgs");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin),100 * 1024 * 1024);
            String email, uname, message, tmp,type;
            while ((email = br.readLine()) != null) {
                uname = br.readLine();
                type = br.readLine();
                tmp = br.readLine();
                message = br.readLine();
                chats.addMessage(uname, email, new Message(Message.type.valueOf(type),Base64.decode(message,Base64.NO_WRAP)), Timestamp.valueOf(tmp.substring(0, 10) + " " + tmp.substring(11, 19)));
            }
            br.close();
            fin.close();
            */
            MsgDatabase db;
            try {
                db = Room.databaseBuilder(this, MsgDatabase.class, "msg.db")
                        .createFromFile(getDatabasePath("msg.db")).build();
                List<Messages> msgs = db.messagesDao().getAllSent(false);
                for(Messages Msg:msgs){
                    chats.addMessage(
                            Msg.uname,Msg.email,new Message(Msg.type,Base64.decode(Msg.message.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP)),Timestamp.valueOf(Msg.tmp)
                    );
                }
                db.close();
            }catch(Exception e){
                db = Room.databaseBuilder(this,MsgDatabase.class,"msg.db").build();
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /*
            FileInputStream fin = openFileInput("sm");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email, uname, message, tmp,type;
            while ((email = br.readLine()) != null) {
                uname = br.readLine();
                type = br.readLine();
                tmp = br.readLine();
                message = br.readLine();
                chats.sendMessage(uname, email,  new Message(Message.type.valueOf(type),Base64.decode(message,Base64.NO_WRAP)), Timestamp.valueOf(tmp.substring(0, 10) + " " + tmp.substring(11, 19)));
            }
            br.close();
            fin.close();
            */
            MsgDatabase db;
            try {
                db = Room.databaseBuilder(this, MsgDatabase.class, "msg.db")
                        .createFromFile(getDatabasePath("msg.db")).build();
                List<Messages> msgs = db.messagesDao().getAllSent(true);
                for(Messages Msg:msgs){
                    chats.sendMessage(
                            Msg.uname,Msg.email,new Message(Msg.type,Base64.decode(Msg.message.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP)),Timestamp.valueOf(Msg.tmp)
                    );
                }
                db.close();
            }catch(Exception e){
                db = Room.databaseBuilder(this,MsgDatabase.class,"msg.db").build();
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkdbStored(String email) {
        String chkmail;
        try {
            chkmail = new BufferedReader(new InputStreamReader(openFileInput("usr"))).readLine();
        } catch (IOException e) {
            return false;
        }
        if (chkmail == null) return false;
        return chkmail.equals(email);
    }

}