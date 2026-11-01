package com.example.bookborrow.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room 数据库主类
 */
@Database(entities = {
    BookEntity.class,
    ReaderEntity.class,
    AdminEntity.class,
    BorrowRecordEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract BookDao bookDao();
    public abstract ReaderDao readerDao();
    public abstract AdminDao adminDao();
    public abstract BorrowRecordDao borrowRecordDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "book_borrow_db"
            ).fallbackToDestructiveMigration()
             .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}