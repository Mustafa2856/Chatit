package com.example.chatit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class Messages {
    @PrimaryKey(autoGenerate = true)
    public int key;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "uname")
    public String uname;

    @ColumnInfo(name = "type")
    public Message.type type;

    @ColumnInfo(name = "timestamp")
    public String tmp;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "sent")
    boolean Sent;
}