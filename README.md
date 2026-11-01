# 图书借阅Android APP - 项目说明文档

---

## 一、项目分析

### 1.1 项目背景
随着移动互联网的发展，图书管理系统逐渐向移动端转移。本项目旨在开发一个完整的图书借阅Android应用，包含**读者端**和**管理员端**双系统，模拟真实图书馆借阅管理流程，适合作为《Android移动开发基础案例教程》课程实验项目。

### 1.2 需求分析
- **用户角色**：
  - **读者**：浏览图书、借阅/归还图书、查看个人借阅记录/历史
  - **管理员**：图书增删改查、全员借阅记录管理、确认还书、逾期图书管理、读者账号管理
- **核心功能**：双角色登录、图书管理、借阅流程、记录管理、逾期预警
- **数据存储**：内存模拟数据（Application单例），APP重启数据清空，无需数据库/服务器
- **页面结构**：主页(角色选择) + 读者端5页 + 管理员端6页 = 共12个Activity

### 1.3 技术选型
| 技术点 | 选择方案 | 理由 |
|--------|----------|------|
| 开发语言 | Java | 课程要求，传统Android开发主流语言 |
| UI布局 | XML + ViewBinding | 传统XML布局，符合教材要求 |
| 列表展示 | RecyclerView | 性能优于ListView，支持复杂布局 |
| 依赖管理 | build.gradle (Groovy) | 避免TOML语法报错，兼容性好 |
| 架构模式 | MVC简化版 | Activity处理逻辑，Application管理全局数据 |
| 最低SDK | API 21 (Android 5.0) | 覆盖99%+设备，支持Material Design |
| UI组件 | Material Components | Toolbar、CardView、TabLayout、TextInputLayout等 |

---

## 二、功能说明

### 2.1 角色与页面结构

```
启动APP → 主页(角色选择)
    ├─ 👤 读者登录 → 读者主页
    │       ├─ 📚 浏览图书 → 图书列表 → 图书详情 → 借阅
    │       ├─ 📋 我的借阅 → 借阅中列表 → 归还
    │       └─ 📖 借阅历史 → 全部记录(含已归还)
    └─ 🔐 管理员登录 → 管理员主页
            ├─ 📚 图书管理 → 列表/搜索/新增/编辑/删除
            ├─ 📋 借阅记录管理 → 全部/借阅中/已归还 + 确认还书
            ├─ 👥 读者管理 → 列表/新增/重置密码
            └─ ⚠️ 逾期图书管理 → 逾期列表 + 确认归还
```

### 2.2 功能清单

#### 读者端
| 编号 | 页面 | 功能点 |
|------|------|--------|
| R1 | 读者登录 | 账号密码登录，默认 student1/123456 |
| R2 | 读者主页 | 欢迎信息、借阅统计、三大功能入口 |
| R3 | 图书列表 | 搜索、卡片展示(封面/书名/作者/出版社/库存) |
| R4 | 图书详情 | 大图封面、完整信息、借阅按钮(库存控制) |
| R5 | 我的借阅 | 仅显示借阅中、归还按钮、库存同步+1 |
| R6 | 借阅历史 | 显示全部记录、状态标签(借阅中/已归还)、归还时间 |

#### 管理员端
| 编号 | 页面 | 功能点 |
|------|------|--------|
| A1 | 管理员登录 | 账号密码登录，默认 admin/123456 |
| A2 | 管理员主页 | 四大功能卡片入口、退出登录 |
| A3 | 图书管理 | 实时搜索、新增/编辑/删除(含校验)、库存联动 |
| A4 | 借阅记录管理 | Tab筛选(全部/借阅中/已归还)、确认还书、逾期标红 |
| A5 | 读者管理 | 列表展示、新增读者账号、重置密码、查看借阅数 |
| A6 | 逾期图书管理 | 仅显示逾期、显示逾期天数、一键确认归还 |

### 2.3 核心数据模型

**Book (图书类)**
```java
- id: int                    // 图书ID
- title: String              // 书名
- author: String             // 作者
- publisher: String          // 出版社
- description: String        // 简介
- coverResId: int            // 封面资源ID
- totalCount: int            // 总库存
- availableCount: int        // 可借数量
```

**Reader (读者类)**
```java
- id: int                    // 读者ID
- username: String           // 登录账号
- password: String           // 登录密码
- realName: String           // 真实姓名
- phone: String              // 电话
- email: String              // 邮箱
- borrowRecords: List        // 借阅记录列表
```

**Admin (管理员类)**
```java
- id: int
- username: String
- password: String
- realName: String
- phone: String
```

**BorrowRecord (借阅记录类)**
```java
- id: int
- book: Book
- reader: Reader             // 关联借阅人
- borrowTime: long           // 借阅时间
- dueTime: long              // 应还时间(借阅后14天)
- status: Enum(BORROWED/RETURNED)
- returnTime: long           // 实际归还时间
- isOverdue(): boolean       // 逾期判断
- getRemainingDays(): int    // 剩余/逾期天数
```

---

## 三、开发环境

### 3.1 硬件环境
- CPU: Intel i5 / AMD Ryzen 5 以上
- 内存: 8GB 以上 (建议 16GB)
- 硬盘: 10GB 可用空间 (SSD优先)

### 3.2 软件环境
| 软件 | 版本 | 说明 |
|------|------|------|
| OS | Windows 10/11 / macOS / Linux | 64位 |
| Android Studio | Koala / Ladybug / Meerkat | 最新稳定版 |
| JDK | 17 (内置) | Android Studio自带 jbr |
| Gradle | 8.3 | gradle-wrapper.properties 指定 |
| Android SDK | API 34 (编译), API 21 (最低) | 通过SDK Manager安装 |

### 3.3 关键配置
```groovy
// app/build.gradle
compileSdk 34
minSdk 21
targetSdk 34
namespace 'com.example.bookborrow'

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
}

// gradle.properties 关键配置
android.overridePathCheck=true          // 允许中文路径
android.useAndroidX=true                // 启用AndroidX
android.enableJetifier=true             // 自动转换第三方库
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

---

## 四、项目结构

```
BookBorrowApp/
├── build.gradle                 # 项目根构建脚本
├── settings.gradle              # 项目设置
├── gradle.properties            # Gradle属性配置
├── gradle/wrapper/gradle-wrapper.properties  # Gradle 8.3
└── app/
    ├── build.gradle             # 模块构建脚本
    ├── proguard-rules.pro       # 混淆规则
    └── src/
        └── main/
            ├── AndroidManifest.xml  # 清单文件(12个Activity)
            ├── java/
            │   └── com/example/bookborrow/
            │       ├── Entity/
            │       │   ├── Book.java
            │       │   ├── Reader.java
            │       │   ├── Admin.java
            │       │   └── BorrowRecord.java
            │       ├── Application/
            │       │   └── BookApplication.java    # 全局数据管理
            │       ├── Adapter/
            │       │   ├── BookAdapter.java
            │       │   ├── BorrowRecordAdapter.java
            │       │   ├── AdminBookAdapter.java
            │       │   ├── AdminRecordAdapter.java
            │       │   ├── AdminReaderAdapter.java
            │       │   └── AdminOverdueAdapter.java
            │       ├── Main/
            │       │   └── MainActivity.java       # 角色选择页
            │       ├── Reader/
            │       │   ├── ReaderLoginActivity.java
            │       │   ├── ReaderMainActivity.java
            │       │   ├── BookListActivity.java
            │       │   ├── BookDetailActivity.java
            │       │   ├── BorrowRecordActivity.java
            │       │   └── BorrowHistoryActivity.java
            │       └── Admin/
            │           ├── AdminLoginActivity.java
            │           ├── AdminMainActivity.java
            │           ├── AdminBookManageActivity.java
            │           ├── AdminRecordManageActivity.java
            │           ├── AdminReaderManageActivity.java
            │           └── AdminOverdueActivity.java
            └── res/
                ├── layout/           # 14个Activity布局 + 6个Item布局 + 3个Dialog布局
                ├── values/           # strings/colors/themes
                ├── drawable/         # 状态背景、图标
                └── mipmap-*/         # 启动图标
```

---

## 五、核心代码解析

### 5.1 全局数据管理 (BookApplication)
```java
// 单例模式管理所有内存数据
public class BookApplication extends Application {
    private List<Book> bookList;
    private List<BorrowRecord> borrowRecords;
    private List<Admin> adminList;
    private List<Reader> readerList;
    private Admin currentAdmin;
    private Reader currentReader;
    
    // 提供：登录校验、增删改查、借阅/归还、搜索、逾期查询等完整API
}
```

### 5.2 双模式适配器复用
```java
// BorrowRecordAdapter 通过 isHistoryMode 参数复用：
// - false: 我的借阅页(仅借阅中+归还按钮)
// - true:  历史页/管理员页(全部记录+状态标签+无按钮/确认还书按钮)
```

### 5.3 借阅流程与库存同步
```java
// 借阅：app.borrowBook(reader, book) -> 库存-1 + 创建记录(关联reader) + 加入reader记录
// 归还：app.confirmReturn(recordId) -> 库存+1 + 标记RETURNED + 记录归还时间
// 逾期：System.currentTimeMillis() > dueTime 自动判断
```

---

## 六、实验小结

### 6.1 学习收获
1. **双角色权限控制**：通过 Application 维护 `currentAdmin`/`currentReader` 实现登录态管理
2. **复杂RecyclerView适配器复用**：同一 Adapter 通过参数适配 4 种场景(读者借阅中/历史/管理员全部/逾期)
3. **Material Design 组件实战**：Toolbar、TabLayout、TextInputLayout、CardView、CoordinatorLayout
4. **Dialog 表单交互**：AlertDialog + 自定义布局实现新增/编辑/重置密码
5. **内存数据一致性**：同一 Book/Reader 对象在多页面共享，修改即时生效

### 6.2 遇到的问题及解决
| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 中文路径编译失败 | AGP 默认检查非ASCII | `android.overridePathCheck=true` |
| Gradle/JDK 版本不兼容 | JDK 25 > Gradle 8.3 支持上限 | AS Settings → Gradle JDK 选 17 |
| 缺少 namespace 报错 | AGP 7.3+ 强制要求 | `build.gradle` 添加 `namespace` |
| AndroidX 依赖警告 | 未显式声明 | `gradle.properties` 添加 `useAndroidX/enableJetifier` |
| 系统图标找不到 | `ic_menu_report` 被移除 | 替换为 `ic_menu_gallery` 等稳定图标 |
| Toolbar 返回键无效 | 未设置监听 | `toolbar.setNavigationOnClickListener(v -> finish())` |
| 借阅记录不区分用户 | 早期设计无 Reader 关联 | BorrowRecord 新增 `reader` 字段，Application 按用户隔离 |

### 6.3 可扩展方向
1. **持久化存储**：Room Database / SharedPreferences / DataStore
2. **网络层**：Retrofit + OkHttp + Coroutines 对接后端 REST API
3. **图片加载**：Glide / Coil 加载网络封面、支持占位/错误图
4. **扫码借阅**：ML Kit / ZXing 扫描 ISBN 自动录入
5. **消息通知**：WorkManager 定时检查逾期 + Notification 推送
6. **多设备同步**：Firebase Realtime Database / Supabase
7. **单元测试**：JUnit + Mockito + Robolectric 覆盖核心逻辑

---

## 七、运行截图预期

| 页面 | 关键元素 |
|------|----------|
| 主页 | 标题"图书借阅系统"，读者登录/管理员登录两大卡片 |
| 读者登录 | 账号密码输入、登录按钮、默认 student1/123456 |
| 读者主页 | 顶部用户信息栏、三大功能按钮、借阅统计卡片 |
| 图书列表 | 搜索框、卡片列表(封面/书名/作者/出版社/库存) |
| 图书详情 | 大图封面、完整信息、底部固定借阅按钮(库存控制) |
| 我的借阅 | 借阅中列表、每行右侧"归还"按钮、状态标签 |
| 借阅历史 | 全部记录、借阅中/已归还标签、显示归还时间 |
| 管理员登录 | 账号密码输入、默认 admin/123456 |
| 管理员主页 | 四大功能卡片网格、退出登录 |
| 图书管理 | 搜索+新增按钮、列表含编辑/删除、Dialog表单 |
| 借阅记录管理 | Tab筛选、确认还书按钮、逾期红字提示 |
| 读者管理 | 新增读者、重置密码、显示当前借阅数 |
| 逾期管理 | 逾期统计、逾期天数红字、确认归还 |

---

## 八、默认账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 管理员 | admin | 123456 | 系统管理员 |
| 读者 | student1 | 123456 | 张三 |
| 读者 | student2 | 123456 | 李四 |

---

## 九、提交清单

- [x] 项目源码完整 (D:\图书借阅APP)
- [x] 可在 Android Studio 直接打开编译运行
- [x] 无 TOML 配置，纯 `build.gradle` 依赖管理
- [x] 12 个 Activity + 6 个 Adapter + 4 个实体类 + 1 个 Application
- [x] 23 个 XML 布局/Item/Dialog + 资源文件
- [x] 本说明文档 (README.md)
- [x] `gradle.properties` 完整配置(中文路径/AndroidX/JVM优化)
- [x] `gradle/wrapper` 版本锁定(Gradle 8.3)

---

**开发者**：[你的姓名/学号]  
**课程**：Android移动开发基础案例教程  
**日期**：2026年10月  
**版本**：v2.0 (含管理员系统)