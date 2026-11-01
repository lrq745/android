package com.example.bookborrow.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface BorrowRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(BorrowRecordEntity record);

    @Update
    void update(BorrowRecordEntity record);

    @Delete
    void delete(BorrowRecordEntity record);

    @Query("SELECT * FROM borrow_records ORDER BY borrowTime DESC")
    List<BorrowRecordEntity> getAll();

    @Query("SELECT * FROM borrow_records WHERE id = :id")
    BorrowRecordEntity getById(int id);

    @Query("SELECT * FROM borrow_records WHERE readerId = :readerId ORDER BY borrowTime DESC")
    List<BorrowRecordEntity> getByReader(int readerId);

    @Query("SELECT * FROM borrow_records WHERE readerId = :readerId AND status = 0 ORDER BY borrowTime DESC")
    List<BorrowRecordEntity> getBorrowedByReader(int readerId);

    @Query("SELECT * FROM borrow_records WHERE status = 0 ORDER BY borrowTime DESC")
    List<BorrowRecordEntity> getAllBorrowed();

    @Query("SELECT * FROM borrow_records WHERE status = 1 ORDER BY returnTime DESC")
    List<BorrowRecordEntity> getAllReturned();

    @Query("SELECT * FROM borrow_records WHERE status = 0 AND dueTime < :now ORDER BY dueTime ASC")
    List<BorrowRecordEntity> getOverdue(long now);

    @Query("SELECT * FROM borrow_records WHERE bookId = :bookId AND status = 0")
    List<BorrowRecordEntity> getBorrowedByBook(int bookId);

    @Transaction
    @Query("SELECT * FROM borrow_records WHERE id = :recordId AND status = 0")
    BorrowRecordEntity getBorrowedForReturn(int recordId);
}