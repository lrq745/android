package com.example.bookborrow.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(BookEntity book);

    @Update
    void update(BookEntity book);

    @Delete
    void delete(BookEntity book);

    @Query("SELECT * FROM books ORDER BY id DESC")
    List<BookEntity> getAll();

    @Query("SELECT * FROM books WHERE id = :id")
    BookEntity getById(int id);

    @Query("SELECT * FROM books WHERE title LIKE :keyword OR author LIKE :keyword ORDER BY id DESC")
    List<BookEntity> search(String keyword);

    @Query("SELECT * FROM books WHERE availableCount > 0")
    List<BookEntity> getAvailableBooks();
}