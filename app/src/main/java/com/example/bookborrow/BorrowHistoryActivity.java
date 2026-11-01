package com.example.bookborrow;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 借阅历史页面 Activity
 * 显示当前登录读者的所有借阅记录（包括已归还），不提供操作按钮
 */
public class BorrowHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBorrowHistory;
    private TextView tvEmpty;
    private BorrowRecordAdapter historyAdapter;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_history);

        app = (BookApplication) getApplication();

        // 检查登录状态
        if (app.getCurrentReader() == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initToolbar();
        initRecyclerView();
        loadData();
    }

    private void initViews() {
        rvBorrowHistory = findViewById(R.id.rv_borrow_history);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.borrow_history_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvBorrowHistory.setLayoutManager(new LinearLayoutManager(this));
        // isHistoryMode=true: 历史页，显示全部记录，无按钮
        historyAdapter = new BorrowRecordAdapter(app.getRecordsByReader(app.getCurrentReader()), true);
        rvBorrowHistory.setAdapter(historyAdapter);
    }

    private void loadData() {
        List<BorrowRecord> records = app.getRecordsByReader(app.getCurrentReader());
        if (records.isEmpty()) {
            rvBorrowHistory.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBorrowHistory.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            historyAdapter.updateData(records);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}