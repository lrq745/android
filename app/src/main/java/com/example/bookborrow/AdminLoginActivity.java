package com.example.bookborrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 管理员登录页面
 */
public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvBack;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        app = (BookApplication) getApplication();

        initViews();
        initListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_admin_username);
        etPassword = findViewById(R.id.et_admin_password);
        btnLogin = findViewById(R.id.btn_admin_login);
        tvBack = findViewById(R.id.tv_back_to_main);
    }

    private void initListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvBack.setOnClickListener(v -> finish());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Admin admin = app.adminLogin(username, password);
        if (admin != null) {
            Toast.makeText(this, "欢迎，" + admin.getRealName(), Toast.LENGTH_SHORT).show();
            // 跳转到管理员主页
            Intent intent = new Intent(this, AdminMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}