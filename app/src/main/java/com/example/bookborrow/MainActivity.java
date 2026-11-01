package com.example.bookborrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 主页面 Activity - 角色选择页
 * 入口页面，选择进入读者端或管理员端
 */
public class MainActivity extends AppCompatActivity {

    private Button btnReaderLogin;
    private Button btnAdminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
    }

    private void initViews() {
        btnReaderLogin = findViewById(R.id.btn_reader_login);
        btnAdminLogin = findViewById(R.id.btn_admin_login);
    }

    private void initListeners() {
        // 读者登录
        btnReaderLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReaderLoginActivity.class);
            startActivity(intent);
        });

        // 管理员登录
        btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}