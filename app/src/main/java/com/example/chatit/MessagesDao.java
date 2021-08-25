package com.example.chatit;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessagesDao {
    @Query("SELECT * FROM Messages")
    List<Messages> getAll();

    @Query("SELECT * FROM Messages WHERE email = :email")
    List<Messages> getUserAll(String email);

    @Query("SELECT * FROM Messages WHERE Sent = :sent")
    List<Messages> getAllSent(boolean sent);

    @Insert
    void InsertAll(Messages... msgs);

    @Delete
    void delete(Messages msg);
}
