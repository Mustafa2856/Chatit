package com.example.chatit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * Activity to show user chat to the ... user.
 */
public class Chat extends AppCompatActivity {

    private ArrayList<Pair<Pair<Timestamp, Message>, Boolean>> chats;
    private final BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((EditText) findViewById(R.id.chatbox)).setText("");
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        TextView veiw = findViewById(R.id.textView4);
        veiw.setText(this.getIntent().getStringExtra("uname"));
        veiw.setOnClickListener(v -> {
            Intent openusrinfo = new Intent(Chat.this, User_info.class);
            openusrinfo.putExtra("remail", this.getIntent().getStringExtra("remail"));
            openusrinfo.putExtra("uname", this.getIntent().getStringExtra("uname"));
            startActivityForResult(openusrinfo, 0);
        });
        Button mediaSelect = findViewById(R.id.button3);
        mediaSelect.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.media_select, null, false);
            PopupWindow pw = new PopupWindow(view,600,400, true);
            pw.showAsDropDown(mediaSelect, 0, 0, Gravity.TOP);
            Button camera = view.findViewById(R.id.button4);
            camera.setOnClickListener(u -> {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 113);
                    }
                }
            });
        });
        EditText chatbox = findViewById(R.id.chatbox);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(v -> {
            String message = chatbox.getText().toString();
            if (!message.equals("")) {
                Intent i = new Intent(this, ServerConnect.class);
                i.setAction("MESSAGE");
                i.putExtra("email", this.getIntent().getStringExtra("email"));
                i.putExtra("password", this.getIntent().getStringExtra("password"));
                i.putExtra("remail", this.getIntent().getStringExtra("remail"));
                i.putExtra("uname", this.getIntent().getStringExtra("uname"));
                i.putExtra("msg", Base64.encodeToString(message.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
                i.putExtra("type","TEXT");
                ServerConnect.enqueueWork(this, ServerConnect.class, 1000, i);
            }
        });
        updateUI();
        Intent chatsync = new Intent(this, ServerConnect.class);
        chatsync.setAction("CHATS");
        chatsync.putExtra("email", this.getIntent().getStringExtra("email"));
        chatsync.putExtra("password", this.getIntent().getStringExtra("password"));
        ServerConnect.enqueueWork(this, ServerConnect.class, 1000, chatsync);
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("com.example.chatit.CHATSYNC"));
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new Date().toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getPath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 113){
            if (resultCode == RESULT_OK)
            {
                Uri uri = data.getData();
                String path = currentPhotoPath;
                try {
                    FileInputStream fin = new FileInputStream(path);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    int nRead;
                    byte[] dat = new byte[16384];
                    while ((nRead = fin.read(dat, 0, dat.length)) != -1) {
                        bout.write(dat, 0, nRead);
                    }
                    dat = bout.toByteArray();
                    Intent i = new Intent(this, ServerConnect.class);
                    i.setAction("MESSAGE");
                    i.putExtra("email", this.getIntent().getStringExtra("email"));
                    i.putExtra("password", this.getIntent().getStringExtra("password"));
                    i.putExtra("remail", this.getIntent().getStringExtra("remail"));
                    i.putExtra("uname", this.getIntent().getStringExtra("uname"));
                    i.putExtra("msg", Base64.encodeToString(dat, Base64.NO_WRAP));
                    i.putExtra("type","IMG");
                    ServerConnect.enqueueWork(this, ServerConnect.class, 1000, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //TODO
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("com.example.chatit.CHATSYNC"));
        super.onResume();
    }

    private void updateUI() {
        fillChats();
        Collections.sort(chats, (o1, o2) -> o1.first.first.compareTo(o2.first.first));
        LinearLayout chat_display = findViewById(R.id.chatv);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        findViewById(R.id.chat).setBackgroundColor(Color.WHITE);
        chat_display.removeAllViews();
        for (Pair<Pair<Timestamp, Message>, Boolean> chat : chats) {
            if(chat.first.second.tp == Message.type.TEXT){
                TextView txt = new TextView(this);
                txt.setText(String.format("%s\n%s", new String(chat.first.second.data), chat.first.first.toString().substring(11, 16)));
                txt.setTextColor(Color.BLACK);
                txt.setPadding(10, 10, 10, 10);
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(16);
                shape.setColor(Color.argb(255, 211, 247, 198));
                txt.setBackground(shape);
                txt.setLayoutParams(params);
                txt.setEllipsize(TextUtils.TruncateAt.END);
                chat_display.addView(txt);
                if (chat.second) ((LinearLayout.LayoutParams) txt.getLayoutParams()).gravity = Gravity.RIGHT;
                txt.requestLayout();
            }
            else if(chat.first.second.tp == Message.type.IMG){
                ImageView im = new ImageView(this);
                Bitmap bitmap = BitmapFactory.decodeByteArray(chat.first.second.data, 0, chat.first.second.data.length);
                im.setImageBitmap(bitmap);
                im.setPadding(10,10,10,10);
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(16);
                shape.setColor(Color.argb(255, 211, 247, 198));
                im.setBackground(shape);
                im.setLayoutParams(params);
                chat_display.addView(im);
                if (chat.second) ((LinearLayout.LayoutParams) im.getLayoutParams()).gravity = Gravity.RIGHT;
                im.requestLayout();
            }
            else if(chat.first.second.tp == Message.type.AUDIO){
                //TODO
            }
            else if(chat.first.second.tp == Message.type.VIDEO){
                //TODO
            }
            else if(chat.first.second.tp == Message.type.DOC){
                //TODO
            }
            else{
                Log.e("msgType","Invalid msg type: " + chat.first.second.tp.toString());
            }
        }
        findViewById(R.id.chat).post(() -> findViewById(R.id.chat).scrollTo(0, findViewById(R.id.chatv).getBottom()));
    }

    private void fillChats() {
        chats = new ArrayList<>();
        ArrayList<Pair<Timestamp, Message>> userChats = null;
        try{
            userChats = ServerConnect.getChats().messages.get(this.getIntent().getStringExtra("remail"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if (userChats != null) {
            for (Pair<Timestamp, Message> m : userChats) {
                chats.add(new Pair<>(new Pair<>(m.first, m.second), false));
            }
        }
        userChats = ServerConnect.getChats().sent.get(this.getIntent().getStringExtra("remail"));
        if (userChats != null) {
            for (Pair<Timestamp, Message> m : userChats) {
                chats.add(new Pair<>(new Pair<>(m.first, m.second), true));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }
}