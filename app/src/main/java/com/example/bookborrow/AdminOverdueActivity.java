package com.example.bookborrow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员逾期图书管理页面
 */
public class AdminOverdueActivity extends AppCompatActivity {

    private RecyclerView rvOverdue;
    private TextView tvEmpty;
    private TextView tvOverdueCount;
    private TextView tvOverdueTip;
    private AdminOverdueAdapter adapter;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overdue);

        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initRecyclerView();
        loadData();
    }

    private void initViews() {
        rvOverdue = findViewById(R.id.rv_overdue_records);
        tvEmpty = findViewById(R.id.tv_empty);
        tvOverdueCount = findViewById(R.id.tv_overdue_count);
        tvOverdueTip = findViewById(R.id.tv_overdue_tip);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("逾期图书管理");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvOverdue.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOverdueAdapter();
        adapter.setOnConfirmReturnListener(record -> confirmReturn(record));
        rvOverdue.setAdapter(adapter);
    }

    private void loadData() {
        List<BorrowRecord> overdue = app.getOverdueRecords();
        adapter.updateData(overdue);
        tvOverdueCount.setText("逾期记录：" + overdue.size() + " 条");
        tvEmpty.setVisibility(overdue.isEmpty() ? View.VISIBLE : View.GONE);
        rvOverdue.setVisibility(overdue.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmReturn(BorrowRecord record) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认还书")
                .setMessage("读者 " + record.getReaderName() + " 逾期 " + Math.abs(record.getRemainingDays()) + " 天\n确认已归还《" + record.getBookTitle() + "》？")
                .setPositiveButton("确认归还", (dialog, which) -> {
                    boolean success = app.confirmReturn(record.getId());
                    if (success) {
                        Toast.makeText(this, "已确认归还", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    // ========== Adapter ==========
    static class AdminOverdueAdapter extends RecyclerView.Adapter<AdminOverdueAdapter.ViewHolder> {
        private List<BorrowRecord> records = new ArrayList<>();
        private OnConfirmReturnListener listener;

        interface OnConfirmReturnListener { void onConfirmReturn(BorrowRecord record); }

        public AdminOverdueAdapter() {
        }

        public AdminOverdueAdapter(List<BorrowRecord> initialRecords) {
            this.records = new ArrayList<>(initialRecords);
        }

        public void setOnConfirmReturnListener(OnConfirmReturnListener l) { listener = l; }

        public void updateData(List<BorrowRecord> newList) {
            records = new ArrayList<>(newList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_overdue, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(records.get(position));
        }

        @Override public int getItemCount() { return records.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookTitle, tvReaderName, tvReaderUsername, tvBorrowTime, tvDueTime, tvOverdueDays;
            Button btnConfirmReturn;

            ViewHolder(View v) {
                super(v);
                tvBookTitle = v.findViewById(R.id.tv_record_book_title);
                tvReaderName = v.findViewById(R.id.tv_record_reader_name);
                tvReaderUsername = v.findViewById(R.id.tv_record_reader_username);
                tvBorrowTime = v.findViewById(R.id.tv_record_borrow_time);
                tvDueTime = v.findViewById(R.id.tv_record_due_time);
                tvOverdueDays = v.findViewById(R.id.tv_overdue_days);
                btnConfirmReturn = v.findViewById(R.id.btn_confirm_return);
            }

            void bind(BorrowRecord r) {
                tvBookTitle.setText(r.getBookTitle());
                tvReaderName.setText("读者：" + r.getReaderName());
                tvReaderUsername.setText("账号：" + r.getReaderUsername());
                tvBorrowTime.setText("借阅：" + r.getBorrowTimeStr());
                tvDueTime.setText("应还：" + r.getDueTimeStr());

                int overdueDays = Math.abs(r.getRemainingDays());
                tvOverdueDays.setText("已逾期：" + overdueDays + " 天");
                tvOverdueDays.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));

                btnConfirmReturn.setOnClickListener(v -> {
                    if (listener != null) listener.onConfirmReturn(r);
                });
            }
        }
    }
}