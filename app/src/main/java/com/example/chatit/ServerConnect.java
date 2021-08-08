package com.example.chatit;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import java.util.TimeZone;

/**
 * Service to perform all client-server operations.
 */
public class ServerConnect extends JobIntentService {

    static final int JOB_ID = 1000;
    public static Chats chats;
    private static PrivateKey privateKey;

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
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("password")
                        + "&PublicKey=" + intent.getStringExtra("pkey");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null && !response.equals("-1")) {
                        Intent openchatlist = new Intent();
                        openchatlist.setAction("com.exmaple.chatit.OPENCHAT");
                        openchatlist.putExtra("email", intent.getStringExtra("email"));
                        openchatlist.putExtra("password", intent.getStringExtra("password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openchatlist);
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
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
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Username=janedoe&Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("password")
                        + "&PublicKey=" + intent.getStringExtra("pkey");
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
                        openan.setAction("com.exmaple.chatit.OPENASKNAME");
                        openan.putExtra("email", intent.getStringExtra("email"));
                        openan.putExtra("password", intent.getStringExtra("password"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(openan);
                        chats = new Chats();
                        FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                        fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                        fout.write("\n".getBytes(StandardCharsets.UTF_8));
                        fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                        fout.close();
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
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "Username=" + intent.getStringExtra("uname") + "&Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("password");
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
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String timestamp = chats.tmp.toString();
                String data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("password") + "&Timestamp=" + timestamp;
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    if (response != null) {
                        try {
                            JSONArray res = new JSONArray(response);
                            int msgs = res.length();
                            int mode = MODE_PRIVATE;
                            if (checkUserStored(intent.getStringExtra("email"))) mode |= MODE_APPEND;
                            else {
                                chats = new Chats();
                                FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                                fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                                fout.write("\n".getBytes(StandardCharsets.UTF_8));
                                fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                                fout.close();
                            }
                            FileOutputStream fout = openFileOutput("msgs", mode);
                            PrintWriter pw = new PrintWriter(fout);
                            for (int i = 0; i < msgs; i++) {
                                JSONObject obj = res.getJSONObject(i);
                                String email = obj.getJSONObject("sender").getString("email");
                                String uname = obj.getJSONObject("sender").getString("uname");
                                String message = obj.getJSONObject("message").getString("message");
                                Cipher decryptCipher = Cipher.getInstance("RSA");
                                decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
                                byte[] decryptedmessagebytes = decryptCipher.doFinal(MainActivity.hexStringToByteArray(message));
                                message = new String(decryptedmessagebytes);
                                Timestamp tmp = new Timestamp(Timestamp.valueOf(obj.getString("timeStamp").substring(0, 10) + " " + obj.getString("timeStamp").substring(11, 19)).getTime() + TimeZone.getDefault().getRawOffset());
                                chats.addMessage(uname, email, message, tmp);
                                pw.println(email);
                                pw.println(uname);
                                String msg = message;
                                msg = msg.replaceAll("[\r\n]", "\u259f");
                                pw.println(msg);
                                pw.println(tmp);
                            }
                            pw.close();
                            fout.close();
                            if (msgs > 0) {
                                Intent notifyUI = new Intent();
                                notifyUI.setAction("com.example.chatit.CHATSYNC");
                                LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
                            }
                            Intent chats = new Intent(this, ServerConnect.class);
                            chats.setAction("CHATS");
                            chats.putExtra("email", intent.getStringExtra("email"));
                            chats.putExtra("password", intent.getStringExtra("password"));
                            ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chats);
                        } catch (JSONException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (op == Operations.LOADCHATOFFLINE) {
            LoadChats();
            Intent notifyUI = new Intent();
            notifyUI.setAction("com.example.chatit.CHATSYNC");
            LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
        } else if (op == Operations.MESSAGE) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/getpkey");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "email=" + intent.getStringExtra("remail");
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(out);
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    String response;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    response = br.readLine();
                    EncodedKeySpec keySpec = new X509EncodedKeySpec(MainActivity.hexStringToByteArray(response));
                    PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                    url = new URL("https://chatit-server.herokuapp.com/message");
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String msg = intent.getStringExtra("msg");
                    Cipher encryptCipher = Cipher.getInstance("RSA");
                    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    byte[] secretMessageBytes = msg.getBytes(StandardCharsets.UTF_8);
                    msg = MainActivity.toHexString(encryptCipher.doFinal(secretMessageBytes));
                    data = "Email=" + intent.getStringExtra("email") + "&Password=" + intent.getStringExtra("password") + "&ReceiverEmail=" + intent.getStringExtra("remail") + "&message=" + msg;
                    out = data.getBytes(StandardCharsets.UTF_8);
                    stream = connection.getOutputStream();
                    stream.write(out);
                    if (connection.getResponseCode() == 200) {
                        in = connection.getInputStream();
                        br = new BufferedReader(new InputStreamReader(in));
                        response = br.readLine();
                        if (response == null) return;
                        if (!response.equals("2")) return;
                        Timestamp tmp = new Timestamp(System.currentTimeMillis());
                        chats.sendMessage(intent.getStringExtra("uname"), intent.getStringExtra("remail"), intent.getStringExtra("msg"), tmp);
                        int mode = MODE_PRIVATE;
                        if (checkUserStored(intent.getStringExtra("email"))) mode |= MODE_APPEND;
                        else {
                            chats = new Chats();
                            FileOutputStream fout = openFileOutput("usr", MODE_PRIVATE);
                            fout.write(intent.getStringExtra("email").getBytes(StandardCharsets.UTF_8));
                            fout.write("\n".getBytes(StandardCharsets.UTF_8));
                            fout.write(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                            fout.close();
                        }
                        FileOutputStream fout = openFileOutput("sm", mode);
                        PrintWriter pw = new PrintWriter(fout);
                        pw.println(intent.getStringExtra("remail"));
                        pw.println(intent.getStringExtra("uname"));
                        msg = intent.getStringExtra("msg");
                        msg = msg.replaceAll("[\r\n]", "\u259f");
                        pw.println(msg);
                        pw.println(tmp);
                        pw.close();
                        fout.close();
                        Intent notifyUI = new Intent();
                        notifyUI.setAction("com.example.chatit.CHATSYNC");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(notifyUI);
                    }
                }


            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        } else if (op == Operations.FINDUSER) {
            try {
                URL url = new URL("https://chatit-server.herokuapp.com/finduser");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String data = "email=" + intent.getStringExtra("uname");
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
            FileInputStream fin = openFileInput("msgs");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email, uname, message, tmp;
            while ((email = br.readLine()) != null) {
                uname = br.readLine();
                message = br.readLine();
                message = message.replaceAll("\u259f", "\r\n");
                tmp = br.readLine();
                chats.addMessage(uname, email, message, Timestamp.valueOf(tmp.substring(0, 10) + " " + tmp.substring(11, 19)));
            }
            br.close();
            fin.close();
        } catch (IOException e) {
        }
        try {
            FileInputStream fin = openFileInput("sm");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String email, uname, message, tmp;
            while ((email = br.readLine()) != null) {
                uname = br.readLine();
                message = br.readLine();
                message = message.replaceAll("\u259f", "\r\n");
                tmp = br.readLine();
                chats.sendMessage(uname, email, message, Timestamp.valueOf(tmp.substring(0, 10) + " " + tmp.substring(11, 19)));
            }
            br.close();
            fin.close();
        } catch (IOException e) {
        }
    }

    private boolean checkUserStored(String email) {
        String chkmail;
        try {
            chkmail = new BufferedReader(new InputStreamReader(openFileInput("usr"))).readLine();
        } catch (IOException e) {
            return false;
        }
        if (chkmail == null) return false;
        return chkmail.equals(email);
    }

    public enum Operations {LOGIN, REGISTER, CHANGENAME, MESSAGE, CHATS, LOADCHATOFFLINE, FINDUSER}
}