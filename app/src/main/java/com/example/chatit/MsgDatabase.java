package com.example.chatit;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Messages.class},version = 1)
public abstract class MsgDatabase extends RoomDatabase {
    public abstract MessagesDao messagesDao();
}
