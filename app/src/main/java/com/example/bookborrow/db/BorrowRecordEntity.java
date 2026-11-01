package com.example.bookborrow.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 借阅记录实体类 - 对应数据库表
 */
@Entity(tableName = "borrow_records")
public class BorrowRecordEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    public int bookId;           // 图书ID
    public int readerId;         // 读者ID
    public long borrowTime;      // 借阅时间戳
    public long dueTime;         // 应还时间戳
    public long returnTime;      // 归还时间戳 (0 表示未归还)
    public int status;           // 0=借阅中, 1=已归还

    public BorrowRecordEntity() {}

    public BorrowRecordEntity(int bookId, int readerId) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.borrowTime = System.currentTimeMillis();
        this.dueTime = borrowTime + 14L * 24 * 60 * 60 * 1000; // 14天后
        this.status = 0; // 借阅中
    }

    public com.example.bookborrow.BorrowRecord toBorrowRecord(
            com.example.bookborrow.Book book, com.example.bookborrow.Reader reader) {
        com.example.bookborrow.BorrowRecord record = new com.example.bookborrow.BorrowRecord(
            id, book, reader
        );
        // 使用 setter 方法恢复数据库中的时间和状态
        record.setId(id);
        record.setBorrowTime(borrowTime);
        record.setDueTime(dueTime);
        record.setReturnTime(returnTime);
        record.setStatus(status == 0 ? com.example.bookborrow.BorrowRecord.Status.BORROWED : com.example.bookborrow.BorrowRecord.Status.RETURNED);
        return record;
    }

    public static BorrowRecordEntity fromBorrowRecord(com.example.bookborrow.BorrowRecord record) {
        BorrowRecordEntity entity = new BorrowRecordEntity();
        entity.id = record.getId();
        entity.bookId = record.getBook() != null ? record.getBook().getId() : 0;
        entity.readerId = record.getReader() != null ? record.getReader().getId() : 0;
        entity.borrowTime = record.getBorrowTime();
        entity.dueTime = record.getDueTime();
        entity.returnTime = record.getReturnTime();
        entity.status = record.getStatus() == com.example.bookborrow.BorrowRecord.Status.BORROWED ? 0 : 1;
        return entity;
    }
}