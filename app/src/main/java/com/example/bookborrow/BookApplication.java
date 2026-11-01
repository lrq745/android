package com.example.bookborrow;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bookborrow.db.AppDatabase;
import com.example.bookborrow.db.BookEntity;
import com.example.bookborrow.db.ReaderEntity;
import com.example.bookborrow.db.AdminEntity;
import com.example.bookborrow.db.BorrowRecordEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 全局Application类
 * 管理应用级别的数据：图书列表、借阅记录、管理员账号、读者账号
 * 数据持久化存储在 Room 数据库中，APP重启后数据保留
 */
public class BookApplication extends Application {

    private static final String TAG = "BookApplication";

    private AppDatabase db;
    private ExecutorService executorService;
    private Handler mainHandler;

    // 当前登录状态（仅内存保存，不持久化）
    private com.example.bookborrow.Admin currentAdmin;
    private com.example.bookborrow.Reader currentReader;

    @Override
    public void onCreate() {
        super.onCreate();
        db = AppDatabase.getInstance(this);
        executorService = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
        initDefaultData();
    }

    /**
     * 初始化默认数据（仅当数据库为空时）
     */
    private void initDefaultData() {
        executorService.execute(() -> {
            // 检查是否已有数据
            if (db.bookDao().getAll().isEmpty()) {
                Log.d(TAG, "初始化默认数据...");
                initDefaultBooks();
                initDefaultAdmins();
                initDefaultReaders();
            }
        });
    }

    private void initDefaultBooks() {
        List<BookEntity> books = new ArrayList<>();
        books.add(new BookEntity("Android移动开发基础案例教程(第3版)", "张三", "清华大学出版社",
                "本书系统介绍了Android应用开发的基础知识与核心技术，通过大量实战案例详细讲解了Activity、Service、BroadcastReceiver、ContentProvider四大组件的用法，以及UI布局、数据存储、网络编程、多媒体等核心技术。",
                android.R.drawable.ic_menu_gallery, 5));
        books.add(new BookEntity("Java程序设计教程", "李四", "机械工业出版社",
                "本书全面介绍Java语言基础语法、面向对象编程、集合框架、异常处理、IO流、多线程、网络编程、GUI编程等核心内容。",
                android.R.drawable.ic_menu_manage, 3));
        books.add(new BookEntity("数据结构与算法分析", "王五", "电子工业出版社",
                "详细讲解线性表、栈、队列、树、图等经典数据结构，以及排序、查找、动态规划、贪心算法等算法设计技巧。",
                android.R.drawable.ic_menu_agenda, 2));
        books.add(new BookEntity("计算机网络原理与实践", "赵六", "人民邮电出版社",
                "从物理层到应用层系统阐述计算机网络体系结构、TCP/IP协议族、路由算法、网络安全等核心知识。",
                android.R.drawable.ic_menu_directions, 4));
        books.add(new BookEntity("数据库系统原理与MySQL实战", "钱七", "清华大学出版社",
                "涵盖关系数据库理论、SQL语言、数据库设计范式、事务管理、索引优化、存储过程等内容。",
                android.R.drawable.ic_menu_gallery, 1));
        books.add(new BookEntity("操作系统原理与Linux实践", "孙八", "机械工业出版社",
                "深入讲解进程管理、内存管理、文件系统、设备管理、死锁处理等操作系统核心原理。",
                android.R.drawable.ic_menu_help, 0));

        for (BookEntity book : books) {
            db.bookDao().insert(book);
        }
    }

    private void initDefaultAdmins() {
        AdminEntity admin = new AdminEntity("admin", "123456", "系统管理员", "13800138000");
        db.adminDao().insert(admin);
    }

    private void initDefaultReaders() {
        List<ReaderEntity> readers = new ArrayList<>();
        readers.add(new ReaderEntity("student1", "123456", "张三", "13800138001", "zhangsan@school.com"));
        readers.add(new ReaderEntity("student2", "123456", "李四", "13800138002", "lisi@school.com"));
        for (ReaderEntity reader : readers) {
            db.readerDao().insert(reader);
        }
    }

    // ========== 同步执行辅助方法 ==========
    
    /**
     * 在后台线程同步执行数据库操作
     */
    private <T> T runSync(java.util.concurrent.Callable<T> task) {
        try {
            Future<T> future = executorService.submit(task);
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "数据库操作失败", e);
            return null;
        }
    }

    private void runAsync(Runnable task) {
        executorService.execute(task);
    }

    // ========== 当前登录状态管理 ==========

    public com.example.bookborrow.Admin getCurrentAdmin() { return currentAdmin; }
    public com.example.bookborrow.Reader getCurrentReader() { return currentReader; }

    public void setCurrentAdmin(com.example.bookborrow.Admin admin) { this.currentAdmin = admin; }
    public void setCurrentReader(com.example.bookborrow.Reader reader) { this.currentReader = reader; }

    public void logoutAdmin() { this.currentAdmin = null; }
    public void logoutReader() { this.currentReader = null; }

    public boolean isAdminLoggedIn() { return currentAdmin != null; }
    public boolean isReaderLoggedIn() { return currentReader != null; }

    // ========== 管理员账号管理 ==========

    public com.example.bookborrow.Admin adminLogin(String username, String password) {
        AdminEntity entity = runSync(() -> db.adminDao().login(username, password));
        if (entity != null) {
            currentAdmin = entity.toAdmin();
            return currentAdmin;
        }
        return null;
    }

    public List<com.example.bookborrow.Admin> getAllAdmins() {
        List<AdminEntity> entities = runSync(() -> db.adminDao().getAll());
        List<com.example.bookborrow.Admin> result = new ArrayList<>();
        if (entities != null) {
            for (AdminEntity e : entities) result.add(e.toAdmin());
        }
        return result;
    }

    public boolean addAdmin(String username, String password, String realName, String phone) {
        AdminEntity existing = runSync(() -> db.adminDao().getByUsername(username));
        if (existing != null) return false;
        AdminEntity entity = new AdminEntity(username, password, realName, phone);
        long id = runSync(() -> db.adminDao().insert(entity));
        return id > 0;
    }

    public com.example.bookborrow.Admin getAdminByUsername(String username) {
        AdminEntity entity = runSync(() -> db.adminDao().getByUsername(username));
        return entity != null ? entity.toAdmin() : null;
    }

    // ========== 读者账号管理 ==========

    public com.example.bookborrow.Reader readerLogin(String username, String password) {
        ReaderEntity entity = runSync(() -> db.readerDao().login(username, password));
        if (entity != null) {
            currentReader = entity.toReader();
            return currentReader;
        }
        return null;
    }

    public List<com.example.bookborrow.Reader> getAllReaders() {
        List<ReaderEntity> entities = runSync(() -> db.readerDao().getAll());
        List<com.example.bookborrow.Reader> result = new ArrayList<>();
        if (entities != null) {
            for (ReaderEntity e : entities) result.add(e.toReader());
        }
        return result;
    }

    public com.example.bookborrow.Reader getReaderById(int id) {
        ReaderEntity entity = runSync(() -> db.readerDao().getById(id));
        return entity != null ? entity.toReader() : null;
    }

    public com.example.bookborrow.Reader getReaderByUsername(String username) {
        ReaderEntity entity = runSync(() -> db.readerDao().getByUsername(username));
        return entity != null ? entity.toReader() : null;
    }

    public boolean addReader(String username, String password, String realName, String phone, String email) {
        ReaderEntity existing = runSync(() -> db.readerDao().getByUsername(username));
        if (existing != null) return false;
        ReaderEntity entity = new ReaderEntity(username, password, realName, phone, email);
        long id = runSync(() -> db.readerDao().insert(entity));
        return id > 0;
    }

    public boolean updateReader(int readerId, String realName, String phone, String email) {
        ReaderEntity entity = runSync(() -> db.readerDao().getById(readerId));
        if (entity == null) return false;
        entity.realName = realName;
        entity.phone = phone;
        entity.email = email;
        runAsync(() -> db.readerDao().update(entity));
        return true;
    }

    public boolean resetReaderPassword(int readerId, String newPassword) {
        ReaderEntity entity = runSync(() -> db.readerDao().getById(readerId));
        if (entity == null) return false;
        entity.password = newPassword;
        runAsync(() -> db.readerDao().update(entity));
        return true;
    }

    // ========== 图书管理 ==========

    public void addBook(com.example.bookborrow.Book book) {
        BookEntity entity = BookEntity.fromBook(book);
        runAsync(() -> db.bookDao().insert(entity));
    }

    public boolean addBook(String title, String author, String publisher, String description, int coverResId, int totalCount) {
        BookEntity entity = new BookEntity(title, author, publisher, description, coverResId, totalCount);
        long id = runSync(() -> db.bookDao().insert(entity));
        return id > 0;
    }

    public boolean updateBook(int bookId, String title, String author, String publisher, String description, int coverResId, int totalCount) {
        BookEntity entity = runSync(() -> db.bookDao().getById(bookId));
        if (entity == null) return false;
        entity.title = title;
        entity.author = author;
        entity.publisher = publisher;
        entity.description = description;
        entity.coverResId = coverResId;
        entity.totalCount = totalCount;
        runAsync(() -> db.bookDao().update(entity));
        return true;
    }

    public boolean deleteBook(int bookId) {
        BookEntity entity = runSync(() -> db.bookDao().getById(bookId));
        if (entity == null) return false;
        // 检查是否有借阅中的记录
        List<BorrowRecordEntity> borrowed = runSync(() -> db.borrowRecordDao().getBorrowedByBook(bookId));
        if (borrowed != null && !borrowed.isEmpty()) return false;
        runAsync(() -> db.bookDao().delete(entity));
        return true;
    }

    public List<com.example.bookborrow.Book> getBookList() {
        List<BookEntity> entities = runSync(() -> db.bookDao().getAll());
        List<com.example.bookborrow.Book> result = new ArrayList<>();
        if (entities != null) {
            for (BookEntity e : entities) result.add(e.toBook());
        }
        return result;
    }

    public com.example.bookborrow.Book getBookById(int id) {
        BookEntity entity = runSync(() -> db.bookDao().getById(id));
        return entity != null ? entity.toBook() : null;
    }

    public List<com.example.bookborrow.Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getBookList();
        String kw = "%" + keyword.trim() + "%";
        List<BookEntity> entities = runSync(() -> db.bookDao().search(kw));
        List<com.example.bookborrow.Book> result = new ArrayList<>();
        if (entities != null) {
            for (BookEntity e : entities) result.add(e.toBook());
        }
        return result;
    }

    // ========== 借阅记录管理 ==========

    public boolean borrowBook(com.example.bookborrow.Reader reader, com.example.bookborrow.Book book) {
        if (!book.hasStock()) return false;
        
        // 更新图书库存
        BookEntity bookEntity = runSync(() -> db.bookDao().getById(book.getId()));
        if (bookEntity == null || bookEntity.availableCount <= 0) return false;
        
        bookEntity.availableCount--;
        runAsync(() -> db.bookDao().update(bookEntity));
        
        // 创建借阅记录
        BorrowRecordEntity record = new BorrowRecordEntity(book.getId(), reader.getId());
        long recordId = runSync(() -> db.borrowRecordDao().insert(record));
        
        if (recordId > 0) {
            // 更新内存中的 book 对象
            book.setAvailableCount(bookEntity.availableCount);
            return true;
        }
        return false;
    }

    public boolean confirmReturn(int recordId) {
        BorrowRecordEntity record = runSync(() -> db.borrowRecordDao().getBorrowedForReturn(recordId));
        if (record == null) return false;
        
        record.status = 1;
        record.returnTime = System.currentTimeMillis();
        runAsync(() -> db.borrowRecordDao().update(record));
        
        // 更新图书库存
        BookEntity bookEntity = runSync(() -> db.bookDao().getById(record.bookId));
        if (bookEntity != null) {
            bookEntity.availableCount++;
            runAsync(() -> db.bookDao().update(bookEntity));
        }
        return true;
    }

    public List<com.example.bookborrow.BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecordEntity> entities = runSync(() -> db.borrowRecordDao().getAll());
        return convertRecords(entities);
    }

    public List<com.example.bookborrow.BorrowRecord> getBorrowedRecords() {
        List<BorrowRecordEntity> entities = runSync(() -> db.borrowRecordDao().getAllBorrowed());
        return convertRecords(entities);
    }

    public List<com.example.bookborrow.BorrowRecord> getOverdueRecords() {
        List<BorrowRecordEntity> entities = runSync(() -> db.borrowRecordDao().getOverdue(System.currentTimeMillis()));
        return convertRecords(entities);
    }

    public List<com.example.bookborrow.BorrowRecord> getRecordsByReader(com.example.bookborrow.Reader reader) {
        if (reader == null) return new ArrayList<>();
        List<BorrowRecordEntity> entities = runSync(() -> db.borrowRecordDao().getByReader(reader.getId()));
        return convertRecords(entities);
    }

    public List<com.example.bookborrow.BorrowRecord> getBorrowedRecordsByReader(com.example.bookborrow.Reader reader) {
        if (reader == null) return new ArrayList<>();
        List<BorrowRecordEntity> entities = runSync(() -> db.borrowRecordDao().getBorrowedByReader(reader.getId()));
        return convertRecords(entities);
    }

    /**
     * 转换数据库实体为业务对象
     */
    private List<com.example.bookborrow.BorrowRecord> convertRecords(List<BorrowRecordEntity> entities) {
        List<com.example.bookborrow.BorrowRecord> result = new ArrayList<>();
        if (entities == null) return result;
        
        for (BorrowRecordEntity entity : entities) {
            com.example.bookborrow.Book book = getBookById(entity.bookId);
            com.example.bookborrow.Reader reader = getReaderById(entity.readerId);
            if (book != null && reader != null) {
                com.example.bookborrow.BorrowRecord record = entity.toBorrowRecord(book, reader);
                result.add(record);
            }
        }
        return result;
    }
}