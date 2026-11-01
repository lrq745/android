package com.example.bookborrow.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 管理员实体类 - 对应数据库表
 */
@Entity(tableName = "admins")
public class AdminEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public String username;      // 登录账号
    public String password;      // 登录密码
    public String realName;      // 真实姓名
    public String phone;         // 联系电话

    public AdminEntity() {}

    public AdminEntity(@NonNull String username, String password, String realName, String phone) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
    }

    public com.example.bookborrow.Admin toAdmin() {
        com.example.bookborrow.Admin admin = new com.example.bookborrow.Admin(
            id, username, password, realName, phone
        );
        return admin;
    }

    public static AdminEntity fromAdmin(com.example.bookborrow.Admin admin) {
        AdminEntity entity = new AdminEntity();
        entity.id = admin.getId();
        entity.username = admin.getUsername();
        entity.password = admin.getPassword();
        entity.realName = admin.getRealName();
        entity.phone = admin.getPhone();
        return entity;
    }
}