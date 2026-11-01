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
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员借阅记录管理页面
 */
public class AdminRecordManageActivity extends AppCompatActivity {

    private RecyclerView rvRecords;
    private TextView tvEmpty;
    private TabLayout tabFilter;
    private AdminRecordAdapter adapter;
    private BookApplication app;
    private List<BorrowRecord> allRecords = new ArrayList<>();
    private int currentFilter = 0; // 0=全部, 1=借阅中, 2=已归还

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_record_manage);

        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initTabs();
        initRecyclerView();
        loadData();
    }

    private void initViews() {
        rvRecords = findViewById(R.id.rv_admin_records);
        tvEmpty = findViewById(R.id.tv_empty);
        tabFilter = findViewById(R.id.tab_record_filter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("借阅记录管理");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initTabs() {
        tabFilter.addTab(tabFilter.newTab().setText("全部"));
        tabFilter.addTab(tabFilter.newTab().setText("借阅中"));
        tabFilter.addTab(tabFilter.newTab().setText("已归还"));

        tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getPosition();
                filterRecords();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void initRecyclerView() {
        rvRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminRecordAdapter(allRecords);
        adapter.setOnReturnClickListener(record -> confirmReturn(record));
        rvRecords.setAdapter(adapter);
    }

    private void loadData() {
        allRecords = app.getAllBorrowRecords();
        filterRecords();
    }

    private void filterRecords() {
        List<BorrowRecord> filtered = new ArrayList<>();
        for (BorrowRecord r : allRecords) {
            switch (currentFilter) {
                case 0: filtered.add(r); break;
                case 1: if (r.isBorrowed()) filtered.add(r); break;
                case 2: if (!r.isBorrowed()) filtered.add(r); break;
            }
        }
        adapter.updateData(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvRecords.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void confirmReturn(BorrowRecord record) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("确认还书")
                .setMessage("确认读者 " + record.getReaderName() + " 已归还《" + record.getBookTitle() + "》？")
                .setPositiveButton("确认", (dialog, which) -> {
                    boolean success = app.confirmReturn(record.getId());
                    if (success) {
                        Toast.makeText(this, "已确认归还，库存+1", Toast.LENGTH_SHORT).show();
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
    static class AdminRecordAdapter extends RecyclerView.Adapter<AdminRecordAdapter.ViewHolder> {
        private List<BorrowRecord> records = new ArrayList<>();
        private OnReturnClickListener returnListener;

        interface OnReturnClickListener { void onReturnClick(BorrowRecord record); }

        public AdminRecordAdapter(List<BorrowRecord> initialRecords) {
            this.records = new ArrayList<>(initialRecords);
        }

        public void setOnReturnClickListener(OnReturnClickListener l) { returnListener = l; }

        public void updateData(List<BorrowRecord> newList) {
            records = new ArrayList<>(newList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_record, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(records.get(position));
        }

        @Override public int getItemCount() { return records.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookTitle, tvReaderName, tvReaderUsername, tvBorrowTime, tvDueTime, tvReturnTime, tvStatus;
            Button btnConfirmReturn;

            ViewHolder(View v) {
                super(v);
                tvBookTitle = v.findViewById(R.id.tv_record_book_title);
                tvReaderName = v.findViewById(R.id.tv_record_reader_name);
                tvReaderUsername = v.findViewById(R.id.tv_record_reader_username);
                tvBorrowTime = v.findViewById(R.id.tv_record_borrow_time);
                tvDueTime = v.findViewById(R.id.tv_record_due_time);
                tvReturnTime = v.findViewById(R.id.tv_record_return_time);
                tvStatus = v.findViewById(R.id.tv_record_status);
                btnConfirmReturn = v.findViewById(R.id.btn_confirm_return);
            }

            void bind(BorrowRecord r) {
                tvBookTitle.setText(r.getBookTitle());
                tvReaderName.setText("读者：" + r.getReaderName());
                tvReaderUsername.setText("账号：" + r.getReaderUsername());
                tvBorrowTime.setText("借阅：" + r.getBorrowTimeStr());
                tvDueTime.setText("应还：" + r.getDueTimeStr());

                if (r.isBorrowed()) {
                    tvStatus.setText("借阅中");
                    tvStatus.setBackgroundResource(R.drawable.bg_status_borrowed);
                    tvReturnTime.setVisibility(View.GONE);
                    btnConfirmReturn.setVisibility(View.VISIBLE);
                    btnConfirmReturn.setOnClickListener(v -> {
                        if (returnListener != null) returnListener.onReturnClick(r);
                    });

                    // 逾期提示
                    if (r.isOverdue()) {
                        tvDueTime.setText("应还：" + r.getDueTimeStr() + " ⚠️已逾期" + Math.abs(r.getRemainingDays()) + "天");
                        tvDueTime.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                    } else {
                        tvDueTime.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                    }
                } else {
                    tvStatus.setText("已归还");
                    tvStatus.setBackgroundResource(R.drawable.bg_status_returned);
                    tvReturnTime.setVisibility(View.VISIBLE);
                    tvReturnTime.setText("归还：" + r.getReturnTimeStr());
                    btnConfirmReturn.setVisibility(View.GONE);
                }
            }
        }
    }
}