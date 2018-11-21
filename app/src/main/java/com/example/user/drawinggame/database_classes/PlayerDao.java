package com.example.user.drawinggame.database_classes;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface PlayerDao {

    @Insert(onConflict = IGNORE)
    void addPlayer(Player player);

    @Query("SELECT * FROM users WHERE id = :userID")
    Player getPlayerByID(int userID);

    @Query("SELECT * FROM users WHERE account = :serialID")
    Player getPlayerBySerialID(String serialID);

    @Query("SELECT * FROM users")
    List<Player> getAllUsers();

    @Delete
    void deletePlayer(Player player);

    @Query("DELETE FROM users WHERE id = :userID")
    void deletePlayerByID(int userID);

    @Update(onConflict = IGNORE)
    void updatePlayer(Player player);


}
