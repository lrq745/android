package com.example.bookborrow;

import java.util.ArrayList;
import java.util.List;

/**
 * 读者实体类（扩展：添加账号密码登录功能）
 */
public class Reader {
    private int id;
    private String username;          // 登录账号（学号/工号）
    private String password;          // 登录密码
    private String realName;          // 真实姓名
    private String phone;             // 联系电话
    private String email;             // 邮箱
    private List<BorrowRecord> borrowRecords; // 该读者的借阅记录

    public Reader(int id, String username, String password, String realName, String phone, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.borrowRecords = new ArrayList<>();
    }

    // Getter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRealName() { return realName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }

    // Setter
    public void setPassword(String password) { this.password = password; }
    public void setRealName(String realName) { this.realName = realName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    /**
     * 校验密码
     */
    public boolean checkPassword(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    /**
     * 添加借阅记录
     */
    public void addBorrowRecord(BorrowRecord record) {
        borrowRecords.add(0, record);
    }

    /**
     * 获取当前借阅中的记录
     */
    public List<BorrowRecord> getBorrowedRecords() {
        List<BorrowRecord> borrowed = new ArrayList<>();
        for (BorrowRecord r : borrowRecords) {
            if (r.isBorrowed()) borrowed.add(r);
        }
        return borrowed;
    }

    /**
     * 获取所有历史记录
     */
    public List<BorrowRecord> getAllRecords() {
        return new ArrayList<>(borrowRecords);
    }
}