package com.example.bookborrow;

import android.app.AlertDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员读者管理页面
 */
public class AdminReaderManageActivity extends AppCompatActivity {

    private RecyclerView rvReaders;
    private TextView tvEmpty;
    private Button btnAddReader;
    private AdminReaderAdapter adapter;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reader_manage);

        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initRecyclerView();
        loadData();
    }

    private void initViews() {
        rvReaders = findViewById(R.id.rv_admin_readers);
        tvEmpty = findViewById(R.id.tv_empty);
        btnAddReader = findViewById(R.id.btn_add_reader);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("读者管理");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvReaders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminReaderAdapter();
        adapter.setOnItemActionListener(new AdminReaderAdapter.OnItemActionListener() {
            @Override
            public void onResetPassword(Reader reader) {
                showResetPasswordDialog(reader);
            }

            @Override
            public void onViewRecords(Reader reader) {
                // 跳转到该读者的借阅记录
                // 这里可以复用 BorrowHistoryActivity 但传入特定读者
                Toast.makeText(AdminReaderManageActivity.this, "查看 " + reader.getRealName() + " 的记录", Toast.LENGTH_SHORT).show();
                // 实际项目中可以创建一个专门的页面或复用历史页并传入readerId
            }
        });
        rvReaders.setAdapter(adapter);

        btnAddReader.setOnClickListener(v -> showAddReaderDialog());
    }

    private void loadData() {
        adapter.updateData(app.getAllReaders());
        tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        rvReaders.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private void showAddReaderDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reader_edit, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextInputEditText etUsername = dialogView.findViewById(R.id.et_reader_username);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_reader_password);
        TextInputEditText etRealName = dialogView.findViewById(R.id.et_reader_realname);
        TextInputEditText etPhone = dialogView.findViewById(R.id.et_reader_phone);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_reader_email);
        Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        tvTitle.setText("新增读者");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String realName = etRealName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || realName.isEmpty()) {
                Toast.makeText(this, "请填写必填项", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = app.addReader(username, password, realName, phone, email);
            if (success) {
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadData();
            } else {
                Toast.makeText(this, "添加失败：账号已存在", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showResetPasswordDialog(Reader reader) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);
        Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("重置密码 - " + reader.getRealName())
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String newPwd = etNewPassword.getText().toString().trim();
            String confirmPwd = etConfirmPassword.getText().toString().trim();

            if (newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.length() < 6) {
                Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = app.resetReaderPassword(reader.getId(), newPwd);
            if (success) {
                Toast.makeText(this, "密码重置成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // ========== Adapter ==========
    static class AdminReaderAdapter extends RecyclerView.Adapter<AdminReaderAdapter.ViewHolder> {
        private List<Reader> readers = new ArrayList<>();
        private OnItemActionListener listener;

        interface OnItemActionListener {
            void onResetPassword(Reader reader);
            void onViewRecords(Reader reader);
        }

        public AdminReaderAdapter() {
        }

        public AdminReaderAdapter(List<Reader> initialReaders) {
            this.readers = new ArrayList<>(initialReaders);
        }

        public void setOnItemActionListener(OnItemActionListener l) { listener = l; }

        public void updateData(List<Reader> newList) {
            readers = new ArrayList<>(newList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_reader, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(readers.get(position));
        }

        @Override public int getItemCount() { return readers.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvUsername, tvContact, tvBorrowCount;
            Button btnResetPwd, btnViewRecords;

            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_reader_name);
                tvUsername = v.findViewById(R.id.tv_reader_username);
                tvContact = v.findViewById(R.id.tv_reader_contact);
                tvBorrowCount = v.findViewById(R.id.tv_reader_borrow_count);
                btnResetPwd = v.findViewById(R.id.btn_reset_password);
                btnViewRecords = v.findViewById(R.id.btn_view_records);
            }

            void bind(Reader reader) {
                tvName.setText(reader.getRealName());
                tvUsername.setText("账号：" + reader.getUsername());
                tvContact.setText("电话：" + reader.getPhone() + " | 邮箱：" + reader.getEmail());
                tvBorrowCount.setText("当前借阅：" + reader.getBorrowedRecords().size() + "本");

                btnResetPwd.setOnClickListener(v -> {
                    if (listener != null) listener.onResetPassword(reader);
                });
                btnViewRecords.setOnClickListener(v -> {
                    if (listener != null) listener.onViewRecords(reader);
                });
            }
        }
    }
}