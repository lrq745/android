package com.example.bookborrow.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 图书实体类 - 对应数据库表
 */
@Entity(tableName = "books")
public class BookEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public String title;           // 书名
    public String author;          // 作者
    public String publisher;       // 出版社
    public String description;     // 简介
    public int coverResId;         // 封面资源ID
    public int totalCount;         // 总库存
    public int availableCount;     // 可借数量

    // 无参构造函数 (Room 需要)
    public BookEntity() {}

    // 便捷构造函数
    public BookEntity(@NonNull String title, String author, String publisher, 
                     String description, int coverResId, int totalCount) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.coverResId = coverResId;
        this.totalCount = totalCount;
        this.availableCount = totalCount;
    }

    // 转换为原有的 Book 类
    public com.example.bookborrow.Book toBook() {
        com.example.bookborrow.Book book = new com.example.bookborrow.Book(
            id, title, author, publisher, description, coverResId, totalCount
        );
        book.setAvailableCount(availableCount);
        return book;
    }

    // 从原有 Book 类创建
    public static BookEntity fromBook(com.example.bookborrow.Book book) {
        BookEntity entity = new BookEntity();
        entity.id = book.getId();
        entity.title = book.getTitle();
        entity.author = book.getAuthor();
        entity.publisher = book.getPublisher();
        entity.description = book.getDescription();
        entity.coverResId = book.getCoverResId();
        entity.totalCount = book.getTotalCount();
        entity.availableCount = book.getAvailableCount();
        return entity;
    }
}