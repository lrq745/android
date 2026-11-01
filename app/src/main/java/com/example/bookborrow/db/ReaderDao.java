package com.example.bookborrow.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ReaderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(ReaderEntity reader);

    @Update
    void update(ReaderEntity reader);

    @Delete
    void delete(ReaderEntity reader);

    @Query("SELECT * FROM readers ORDER BY id DESC")
    List<ReaderEntity> getAll();

    @Query("SELECT * FROM readers WHERE id = :id")
    ReaderEntity getById(int id);

    @Query("SELECT * FROM readers WHERE username = :username")
    ReaderEntity getByUsername(String username);

    @Query("SELECT * FROM readers WHERE username = :username AND password = :password")
    ReaderEntity login(String username, String password);
}