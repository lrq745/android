package com.example.bookborrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * 借阅记录适配器
 * 用于RecyclerView展示借阅记录列表，支持借阅中/已归还两种状态
 */
public class BorrowRecordAdapter extends RecyclerView.Adapter<BorrowRecordAdapter.RecordViewHolder> {

    private List<BorrowRecord> recordList;
    private OnReturnClickListener onReturnClickListener;
    private boolean isHistoryMode; // true=历史页(显示全部), false=我的借阅页(仅借阅中)

    public BorrowRecordAdapter(List<BorrowRecord> recordList, boolean isHistoryMode) {
        this.recordList = recordList;
        this.isHistoryMode = isHistoryMode;
    }

    public void setOnReturnClickListener(OnReturnClickListener listener) {
        this.onReturnClickListener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_borrow_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        BorrowRecord record = recordList.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return recordList != null ? recordList.size() : 0;
    }

    public void updateData(List<BorrowRecord> newList) {
        this.recordList = newList;
        notifyDataSetChanged();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle;
        TextView tvBorrowTime;
        TextView tvReturnTime;
        TextView tvStatus;
        Button btnReturn;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tv_record_book_title);
            tvBorrowTime = itemView.findViewById(R.id.tv_record_borrow_time);
            tvReturnTime = itemView.findViewById(R.id.tv_record_return_time);
            tvStatus = itemView.findViewById(R.id.tv_record_status);
            btnReturn = itemView.findViewById(R.id.btn_return);
        }

        public void bind(BorrowRecord record) {
            tvBookTitle.setText(record.getBookTitle());
            tvBorrowTime.setText(String.format(itemView.getContext().getString(R.string.borrow_time), record.getBorrowTimeStr()));

            // 状态显示
            if (record.isBorrowed()) {
                tvStatus.setText("借阅中");
                tvStatus.setBackgroundResource(R.drawable.bg_status_borrowed);
                tvReturnTime.setVisibility(View.GONE);
                
                // 我的借阅页显示归还按钮，历史页不显示
                if (!isHistoryMode) {
                    btnReturn.setVisibility(View.VISIBLE);
                    btnReturn.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && onReturnClickListener != null) {
                            onReturnClickListener.onReturnClick(record, position);
                        }
                    });
                } else {
                    btnReturn.setVisibility(View.GONE);
                }
            } else {
                tvStatus.setText("已归还");
                tvStatus.setBackgroundResource(R.drawable.bg_status_returned);
                tvReturnTime.setVisibility(View.VISIBLE);
                tvReturnTime.setText(String.format("归还时间：%s", record.getReturnTimeStr()));
                btnReturn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 归还按钮点击监听器接口
     */
    public interface OnReturnClickListener {
        void onReturnClick(BorrowRecord record, int position);
    }
}