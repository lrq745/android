package com.example.bookborrow;

/**
 * 管理员实体类
 */
public class Admin {
    private int id;
    private String username;      // 登录账号
    private String password;      // 登录密码
    private String realName;      // 真实姓名
    private String phone;         // 联系电话

    public Admin(int id, String username, String password, String realName, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
    }

    // Getter
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRealName() { return realName; }
    public String getPhone() { return phone; }

    // Setter
    public void setPassword(String password) { this.password = password; }
    public void setRealName(String realName) { this.realName = realName; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * 校验密码
     */
    public boolean checkPassword(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }
}