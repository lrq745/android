package com.example.bookborrow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * 图书详情页面 Activity
 * 展示图书详细信息，提供借阅功能
 */
public class BookDetailActivity extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvBookTitle;
    private TextView tvBookAuthor;
    private TextView tvBookAvailable;
    private TextView tvBookDescription;
    private Button btnBorrow;

    private BookApplication app;
    private Book currentBook;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        app = (BookApplication) getApplication();
        bookId = getIntent().getIntExtra("book_id", -1);
        currentBook = app.getBookById(bookId);

        initViews();
        initToolbar();
        loadBookData();
        initBorrowButton();
    }

    private void initViews() {
        ivBookCover = findViewById(R.id.iv_book_cover_large);
        tvBookTitle = findViewById(R.id.tv_book_title);
        tvBookAuthor = findViewById(R.id.tv_book_author);
        tvBookAvailable = findViewById(R.id.tv_book_available);
        tvBookDescription = findViewById(R.id.tv_book_description);
        btnBorrow = findViewById(R.id.btn_borrow);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.book_detail_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadBookData() {
        if (currentBook == null) {
            Toast.makeText(this, "图书不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBookCover.setImageResource(currentBook.getCoverResId());
        tvBookTitle.setText(currentBook.getTitle());
        tvBookAuthor.setText(String.format(getString(R.string.book_author), currentBook.getAuthor()));
        tvBookAvailable.setText(String.format(getString(R.string.book_available), currentBook.getAvailableCount()));
        tvBookDescription.setText(currentBook.getDescription());

        // 根据库存更新按钮状态
        updateBorrowButtonState();
    }

    private void initBorrowButton() {
        btnBorrow.setOnClickListener(v -> {
            if (currentBook != null && currentBook.getAvailableCount() > 0) {
                // 检查是否已登录
                Reader reader = app.getCurrentReader();
                if (reader == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 借阅图书
                boolean success = app.borrowBook(reader, currentBook);
                if (success) {
                    updateBorrowButtonState();
                    Toast.makeText(this, R.string.borrow_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.borrow_fail_no_stock, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.borrow_fail_no_stock, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据库存更新借阅按钮状态
     */
    private void updateBorrowButtonState() {
        if (currentBook != null && currentBook.getAvailableCount() > 0) {
            btnBorrow.setEnabled(true);
            btnBorrow.setText(R.string.btn_borrow);
            btnBorrow.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
        } else {
            btnBorrow.setEnabled(false);
            btnBorrow.setText("无库存");
            btnBorrow.setBackgroundTintList(getColorStateList(R.color.btn_disabled));
        }
        if (currentBook != null) {
            tvBookAvailable.setText(String.format(getString(R.string.book_available), currentBook.getAvailableCount()));
            if (currentBook.getAvailableCount() <= 0) {
                tvBookAvailable.setTextColor(getColor(R.color.gray_normal));
                tvBookAvailable.setText("已借完");
            } else {
                tvBookAvailable.setTextColor(getColor(R.color.green));
            }
        }
    }
}