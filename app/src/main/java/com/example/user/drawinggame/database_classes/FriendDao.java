package com.example.user.drawinggame.database_classes;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface FriendDao {

    @Insert(onConflict = IGNORE)
    void addFriend(Player player);

    @Query("SELECT * FROM friends WHERE id = :id")
    Friend getFriendByID(int id);

    @Query("SELECT * FROM friends")
    List<Friend> getAllFriends();

    @Delete
    void deleteFriend(Friend friend);

    @Query("DELETE FROM friends WHERE id = :friend_id")
    void deleteFriendByID(int friend_id);

    @Update(onConflict = IGNORE)
    void updateFriend(Friend friend);
}
