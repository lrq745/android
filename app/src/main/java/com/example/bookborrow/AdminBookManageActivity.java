package com.example.bookborrow;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
 * 管理员图书管理页面
 */
public class AdminBookManageActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private TextView tvEmpty;
    private TextInputEditText etSearch;
    private Button btnAddBook;
    private AdminBookAdapter adapter;
    private BookApplication app;
    private List<Book> allBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_manage);

        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initRecyclerView();
        initSearch();
        loadData();
    }

    private void initViews() {
        rvBooks = findViewById(R.id.rv_admin_books);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search_book);
        btnAddBook = findViewById(R.id.btn_add_book);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("图书管理");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBookAdapter(allBooks);
        adapter.setOnItemActionListener(new AdminBookAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Book book) {
                showEditDialog(book);
            }

            @Override
            public void onDelete(Book book) {
                confirmDelete(book);
            }
        });
        rvBooks.setAdapter(adapter);
    }

    private void initSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAddBook.setOnClickListener(v -> showAddDialog());
    }

    private void loadData() {
        allBooks = app.getBookList();
        filterBooks(etSearch.getText().toString());
    }

    private void filterBooks(String keyword) {
        List<Book> filtered;
        if (keyword == null || keyword.trim().isEmpty()) {
            filtered = allBooks;
        } else {
            filtered = app.searchBooks(keyword);
        }
        adapter.updateData(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvBooks.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showAddDialog() {
        showBookDialog(null);
    }

    private void showEditDialog(Book book) {
        showBookDialog(book);
    }

    private void showBookDialog(Book book) {
        boolean isEdit = book != null;
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_edit, null);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextInputEditText etTitle = dialogView.findViewById(R.id.et_book_title);
        TextInputEditText etAuthor = dialogView.findViewById(R.id.et_book_author);
        TextInputEditText etPublisher = dialogView.findViewById(R.id.et_book_publisher);
        TextInputEditText etTotalCount = dialogView.findViewById(R.id.et_book_total_count);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_book_description);
        Button btnCancel = dialogView.findViewById(R.id.btn_dialog_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        if (isEdit) {
            tvTitle.setText("编辑图书");
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etPublisher.setText(book.getPublisher());
            etTotalCount.setText(String.valueOf(book.getTotalCount()));
            etDescription.setText(book.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String author = etAuthor.getText().toString().trim();
            String publisher = etPublisher.getText().toString().trim();
            String countStr = etTotalCount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || author.isEmpty() || countStr.isEmpty()) {
                Toast.makeText(this, "请填写必填项", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalCount;
            try {
                totalCount = Integer.parseInt(countStr);
                if (totalCount < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "库存必须是非负整数", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (isEdit) {
                success = app.updateBook(book.getId(), title, author, publisher, description,
                        book.getCoverResId(), totalCount);
                if (success) Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                success = app.addBook(title, author, publisher, description, android.R.drawable.ic_menu_gallery, totalCount);
                if (success) Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            }

            if (success) {
                dialog.dismiss();
                loadData();
            } else {
                Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void confirmDelete(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("删除确认")
                .setMessage("确定要删除《" + book.getTitle() + "》吗？\n(有借阅记录的图书无法删除)")
                .setPositiveButton("删除", (dialog, which) -> {
                    boolean success = app.deleteBook(book.getId());
                    if (success) {
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "删除失败：该图书有未归还的借阅记录", Toast.LENGTH_SHORT).show();
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
    static class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.ViewHolder> {
        private List<Book> books = new ArrayList<>();
        private OnItemActionListener listener;

        interface OnItemActionListener {
            void onEdit(Book book);
            void onDelete(Book book);
        }

        public AdminBookAdapter(List<Book> initialBooks) {
            this.books = new ArrayList<>(initialBooks);
        }

        public void setOnItemActionListener(OnItemActionListener l) { listener = l; }

        public void updateData(List<Book> newList) {
            books = new ArrayList<>(newList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_book, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(books.get(position));
        }

        @Override
        public int getItemCount() { return books.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCover;
            TextView tvTitle, tvAuthor, tvStock;
            Button btnEdit, btnDelete;

            ViewHolder(View v) {
                super(v);
                ivCover = v.findViewById(R.id.iv_book_cover);
                tvTitle = v.findViewById(R.id.tv_book_title);
                tvAuthor = v.findViewById(R.id.tv_book_author);
                tvStock = v.findViewById(R.id.tv_book_stock);
                btnEdit = v.findViewById(R.id.btn_edit_book);
                btnDelete = v.findViewById(R.id.btn_delete_book);
            }

            void bind(Book book) {
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor() + " / " + book.getPublisher());
                tvStock.setText("库存：" + book.getTotalCount() + " / 可借：" + book.getAvailableCount());
                ivCover.setImageResource(book.getCoverResId());

                // 库存颜色
                if (book.getAvailableCount() <= 0) {
                    tvStock.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                } else {
                    tvStock.setTextColor(itemView.getContext().getColor(R.color.green));
                }

                btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(book);
                });
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(book);
                });
            }
        }
    }
}