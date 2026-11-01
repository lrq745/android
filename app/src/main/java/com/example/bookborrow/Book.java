package com.example.bookborrow;

/**
 * 图书实体类
 * 包含图书的基本信息：ID、书名、作者、出版社、简介、封面图片资源ID、总库存、可借数量
 */
public class Book {
    private int id;                    // 图书ID
    private String title;              // 书名
    private String author;             // 作者
    private String publisher;          // 出版社
    private String description;        // 图书简介
    private int coverResId;            // 封面图片资源ID (drawable资源)
    private int totalCount;            // 总库存数量
    private int availableCount;        // 当前可借数量

    public Book(int id, String title, String author, String publisher, String description, int coverResId, int totalCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.coverResId = coverResId;
        this.totalCount = totalCount;
        this.availableCount = totalCount; // 初始可借数量等于总库存
    }

    // Getter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getDescription() { return description; }
    public int getCoverResId() { return coverResId; }
    public int getTotalCount() { return totalCount; }
    public int getAvailableCount() { return availableCount; }

    // Setter 方法（管理员编辑用）
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setDescription(String description) { this.description = description; }
    public void setCoverResId(int coverResId) { this.coverResId = coverResId; }
    public void setTotalCount(int totalCount) {
        int diff = totalCount - this.totalCount;
        this.totalCount = totalCount;
        this.availableCount += diff; // 同步调整可借数量
        if (this.availableCount < 0) this.availableCount = 0;
    }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    /**
     * 借阅图书：库存减1
     * @return 是否借阅成功
     */
    public boolean borrow() {
        if (availableCount > 0) {
            availableCount--;
            return true;
        }
        return false;
    }

    /**
     * 归还图书：库存加1
     */
    public void returnBook() {
        if (availableCount < totalCount) {
            availableCount++;
        }
    }

    /**
     * 是否有库存
     */
    public boolean hasStock() {
        return availableCount > 0;
    }
}