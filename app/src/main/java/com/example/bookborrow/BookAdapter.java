package com.example.bookborrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 图书列表适配器
 * 用于RecyclerView展示图书列表数据
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnItemClickListener onItemClickListener;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    /**
     * 设置条目点击监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    /**
     * 更新数据并刷新
     */
    public void updateData(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder 类，缓存视图引用
     */
    class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvBookAvailable;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvBookAvailable = itemView.findViewById(R.id.tv_book_available);

            // 设置整个条目点击事件
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(bookList.get(position));
                }
            });
        }

        public void bind(Book book) {
            tvBookTitle.setText(book.getTitle());
            tvBookAuthor.setText(String.format(itemView.getContext().getString(R.string.book_author), book.getAuthor()));
            tvBookAvailable.setText(String.format(itemView.getContext().getString(R.string.book_available), book.getAvailableCount()));
            ivBookCover.setImageResource(book.getCoverResId());

            // 库存为0时显示灰色
            if (book.getAvailableCount() <= 0) {
                tvBookAvailable.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
                tvBookAvailable.setText("已借完");
            } else {
                tvBookAvailable.setTextColor(itemView.getContext().getColor(R.color.green));
            }
        }
    }

    /**
     * 条目点击监听器接口
     */
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }
}