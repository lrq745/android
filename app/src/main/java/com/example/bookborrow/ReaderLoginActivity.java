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
 * 读者登录页面
 */
public class ReaderLoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvBack;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_login);

        app = (BookApplication) getApplication();

        initViews();
        initListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_reader_username);
        etPassword = findViewById(R.id.et_reader_password);
        btnLogin = findViewById(R.id.btn_reader_login);
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

        Reader reader = app.readerLogin(username, password);
        if (reader != null) {
            Toast.makeText(this, "欢迎，" + reader.getRealName(), Toast.LENGTH_SHORT).show();
            // 跳转到读者主页（原 MainActivity 功能）
            Intent intent = new Intent(this, ReaderMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}