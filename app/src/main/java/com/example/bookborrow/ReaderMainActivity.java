package com.example.bookborrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 读者主页（登录后）
 */
public class ReaderMainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvUsername;
    private TextView tvBorrowStats;
    private Button btnBrowseBooks;
    private Button btnMyBorrow;
    private Button btnBorrowHistory;
    private Button btnLogout;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_main);

        app = (BookApplication) getApplication();

        initViews();
        initData();
        initListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_reader_welcome);
        tvUsername = findViewById(R.id.tv_reader_username);
        tvBorrowStats = findViewById(R.id.tv_borrow_stats);
        btnBrowseBooks = findViewById(R.id.btn_browse_books);
        btnMyBorrow = findViewById(R.id.btn_my_borrow);
        btnBorrowHistory = findViewById(R.id.btn_borrow_history);
        btnLogout = findViewById(R.id.btn_reader_logout);
    }

    private void initData() {
        if (app.getCurrentReader() != null) {
            Reader reader = app.getCurrentReader();
            tvWelcome.setText("欢迎，" + reader.getRealName());
            tvUsername.setText("账号：" + reader.getUsername());

            int borrowed = reader.getBorrowedRecords().size();
            int returned = reader.getAllRecords().size() - borrowed;
            tvBorrowStats.setText("借阅中：" + borrowed + "本 | 已归还：" + returned + "本");
        }
    }

    private void initListeners() {
        btnBrowseBooks.setOnClickListener(v -> {
            startActivity(new Intent(this, BookListActivity.class));
        });

        btnMyBorrow.setOnClickListener(v -> {
            startActivity(new Intent(this, BorrowRecordActivity.class));
        });

        btnBorrowHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, BorrowHistoryActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            app.logoutReader();
            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onBackPressed() {
        // 禁止返回键退出到登录页，必须点击退出登录
        // super.onBackPressed();
    }
}