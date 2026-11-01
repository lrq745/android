package com.example.bookborrow.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface AdminDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(AdminEntity admin);

    @Update
    void update(AdminEntity admin);

    @Delete
    void delete(AdminEntity admin);

    @Query("SELECT * FROM admins ORDER BY id DESC")
    List<AdminEntity> getAll();

    @Query("SELECT * FROM admins WHERE id = :id")
    AdminEntity getById(int id);

    @Query("SELECT * FROM admins WHERE username = :username")
    AdminEntity getByUsername(String username);

    @Query("SELECT * FROM admins WHERE username = :username AND password = :password")
    AdminEntity login(String username, String password);
}