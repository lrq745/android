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
 * 我的借阅记录页面 Activity
 * 仅显示当前登录读者借阅中的记录，提供归还功能
 */
public class BorrowRecordActivity extends AppCompatActivity implements BorrowRecordAdapter.OnReturnClickListener {

    private RecyclerView rvBorrowRecords;
    private TextView tvEmpty;
    private BorrowRecordAdapter recordAdapter;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_record);

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
        rvBorrowRecords = findViewById(R.id.rv_borrow_records);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.borrow_record_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvBorrowRecords.setLayoutManager(new LinearLayoutManager(this));
        // isHistoryMode=false: 我的借阅页，显示归还按钮
        recordAdapter = new BorrowRecordAdapter(app.getBorrowedRecordsByReader(app.getCurrentReader()), false);
        recordAdapter.setOnReturnClickListener(this);
        rvBorrowRecords.setAdapter(recordAdapter);
    }

    private void loadData() {
        List<BorrowRecord> records = app.getBorrowedRecordsByReader(app.getCurrentReader());
        if (records.isEmpty()) {
            rvBorrowRecords.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBorrowRecords.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            recordAdapter.updateData(records);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onReturnClick(BorrowRecord record, int position) {
        boolean success = app.confirmReturn(record.getId());
        if (success) {
            recordAdapter.updateData(app.getBorrowedRecordsByReader(app.getCurrentReader()));
            Toast.makeText(this, R.string.return_success, Toast.LENGTH_SHORT).show();
        }
    }
}