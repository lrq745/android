package com.example.bookborrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * 管理员主页
 */
public class AdminMainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnBookManage;
    private Button btnRecordManage;
    private Button btnReaderManage;
    private Button btnOverdueManage;
    private Button btnLogout;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initData();
        initListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_admin_welcome);
        btnBookManage = findViewById(R.id.btn_book_manage);
        btnRecordManage = findViewById(R.id.btn_record_manage);
        btnReaderManage = findViewById(R.id.btn_reader_manage);
        btnOverdueManage = findViewById(R.id.btn_overdue_manage);
        btnLogout = findViewById(R.id.btn_admin_logout);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("管理员主页");
        }
    }

    private void initData() {
        if (app.getCurrentAdmin() != null) {
            tvWelcome.setText("欢迎，" + app.getCurrentAdmin().getRealName());
        }
    }

    private void initListeners() {
        btnBookManage.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookManageActivity.class));
        });

        btnRecordManage.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminRecordManageActivity.class));
        });

        btnReaderManage.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminReaderManageActivity.class));
        });

        btnOverdueManage.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminOverdueActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            app.logoutAdmin();
            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // 禁止返回键退出，必须点击退出登录
        // super.onBackPressed();
    }
}