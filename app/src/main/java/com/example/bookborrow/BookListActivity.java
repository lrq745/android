package com.example.bookborrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 图书列表页面 Activity
 * 使用 RecyclerView 展示所有图书，点击条目跳转到详情页
 */
public class BookListActivity extends AppCompatActivity implements BookAdapter.OnItemClickListener {

    private RecyclerView rvBookList;
    private TextView tvEmpty;
    private BookAdapter bookAdapter;
    private BookApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // 获取全局 Application 实例
        app = (BookApplication) getApplication();

        initViews();
        initToolbar();
        initRecyclerView();
        loadData();
    }

    private void initViews() {
        rvBookList = findViewById(R.id.rv_book_list);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.book_list_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        // 设置布局管理器
        rvBookList.setLayoutManager(new LinearLayoutManager(this));

        // 创建适配器并设置点击监听
        bookAdapter = new BookAdapter(app.getBookList());
        bookAdapter.setOnItemClickListener(this);
        rvBookList.setAdapter(bookAdapter);
    }

    private void loadData() {
        List<Book> books = app.getBookList();
        if (books.isEmpty()) {
            rvBookList.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvBookList.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            bookAdapter.updateData(books);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 返回时刷新数据（可能有库存变化）
        loadData();
    }

    @Override
    public void onItemClick(Book book) {
        // 跳转到图书详情页，传递图书ID
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book_id", book.getId());
        startActivity(intent);
    }
}