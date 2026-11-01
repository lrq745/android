package com.example.bookborrow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 借阅记录实体类
 * 记录用户借阅的图书信息、借阅时间、归还时间、状态、借阅人
 */
public class BorrowRecord {
    public enum Status {
        BORROWED,   // 借阅中
        RETURNED    // 已归还
    }

    private int id;                    // 记录ID
    private Book book;                 // 借阅的图书对象
    private Reader reader;             // 借阅人
    private long borrowTime;           // 借阅时间戳
    private String borrowTimeStr;      // 格式化后的借阅时间字符串
    private Status status;             // 状态：借阅中/已归还
    private long returnTime;           // 归还时间戳
    private String returnTimeStr;      // 格式化后的归还时间字符串
    private long dueTime;              // 应还时间戳（借阅后14天）
    private String dueTimeStr;         // 格式化后的应还时间字符串

    public BorrowRecord(int id, Book book, Reader reader) {
        this.id = id;
        this.book = book;
        this.reader = reader;
        this.borrowTime = System.currentTimeMillis();
        this.status = Status.BORROWED;
        // 格式化时间：yyyy-MM-dd HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.borrowTimeStr = sdf.format(new Date(borrowTime));
        // 应还时间：借阅后14天
        this.dueTime = borrowTime + 14L * 24 * 60 * 60 * 1000;
        this.dueTimeStr = sdf.format(new Date(dueTime));
    }

    // Getter 方法
    public int getId() { return id; }
    public Book getBook() { return book; }
    public Reader getReader() { return reader; }
    public long getBorrowTime() { return borrowTime; }
    public String getBorrowTimeStr() { return borrowTimeStr; }
    public Status getStatus() { return status; }
    public long getReturnTime() { return returnTime; }
    public String getReturnTimeStr() { return returnTimeStr; }
    public long getDueTime() { return dueTime; }
    public String getDueTimeStr() { return dueTimeStr; }
    public String getBookTitle() { return book != null ? book.getTitle() : ""; }
    public String getReaderName() { return reader != null ? reader.getRealName() : ""; }
    public String getReaderUsername() { return reader != null ? reader.getUsername() : ""; }

    // Setter 方法（用于数据库恢复）
    public void setId(int id) { this.id = id; }
    public void setBook(Book book) { this.book = book; }
    public void setReader(Reader reader) { this.reader = reader; }
    public void setBorrowTime(long borrowTime) { 
        this.borrowTime = borrowTime; 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.borrowTimeStr = sdf.format(new Date(borrowTime));
    }
    public void setDueTime(long dueTime) { 
        this.dueTime = dueTime; 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.dueTimeStr = sdf.format(new Date(dueTime));
    }
    public void setReturnTime(long returnTime) { 
        this.returnTime = returnTime; 
        if (returnTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            this.returnTimeStr = sdf.format(new Date(returnTime));
        }
    }
    public void setStatus(Status status) { this.status = status; }
    public void setBorrowTimeStr(String s) { this.borrowTimeStr = s; }
    public void setDueTimeStr(String s) { this.dueTimeStr = s; }
    public void setReturnTimeStr(String s) { this.returnTimeStr = s; }

    /**
     * 标记为已归还
     */
    public void markReturned() {
        this.status = Status.RETURNED;
        this.returnTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.returnTimeStr = sdf.format(new Date(returnTime));
    }

    /**
     * 是否正在借阅中
     */
    public boolean isBorrowed() {
        return status == Status.BORROWED;
    }

    /**
     * 是否逾期（借阅中且超过应还时间）
     */
    public boolean isOverdue() {
        return status == Status.BORROWED && System.currentTimeMillis() > dueTime;
    }

    /**
     * 剩余天数（负数表示已逾期天数）
     */
    public int getRemainingDays() {
        long diff = dueTime - System.currentTimeMillis();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
}