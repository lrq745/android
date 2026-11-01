package com.example.bookborrow.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 读者实体类 - 对应数据库表
 */
@Entity(tableName = "readers")
public class ReaderEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public String username;      // 登录账号
    public String password;      // 登录密码
    public String realName;      // 真实姓名
    public String phone;         // 联系电话
    public String email;         // 邮箱

    // 无参构造函数
    public ReaderEntity() {}

    // 便捷构造函数
    public ReaderEntity(@NonNull String username, String password, String realName, 
                       String phone, String email) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
    }

    public com.example.bookborrow.Reader toReader() {
        com.example.bookborrow.Reader reader = new com.example.bookborrow.Reader(
            id, username, password, realName, phone, email
        );
        return reader;
    }

    public static ReaderEntity fromReader(com.example.bookborrow.Reader reader) {
        ReaderEntity entity = new ReaderEntity();
        entity.id = reader.getId();
        entity.username = reader.getUsername();
        entity.password = reader.getPassword();
        entity.realName = reader.getRealName();
        entity.phone = reader.getPhone();
        entity.email = reader.getEmail();
        return entity;
    }
}