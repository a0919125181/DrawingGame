package com.example.user.drawinggame.database_classes;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface MessageDao {

    @Insert(onConflict = IGNORE)
    void addMessage(Message message);

    @Query("SELECT * FROM messages WHERE :msg_id = msg_id")
    Message getMessageByID(int msg_id);

    @Query("SELECT * FROM messages")
    List<Message> getAllMessage();

    @Query("SELECT * FROM messages WHERE :sender_id = sender_id")
    List<Message> getSomeoneMSG(int sender_id);

    @Delete
    void deleteMessage(Message message);

    @Query("DELETE FROM messages WHERE :msg_id = msg_id")
    void deleteMessageByID(int msg_id);

    @Update(onConflict = IGNORE)
    void updateMessage(Message message);

}
