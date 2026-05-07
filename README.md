# 无人机信息管理系统

基于Spring Boot 2.2.x的无人机信息管理系统，支持无人机信息的增删改查功能。

## 项目演示
系统核心功能演示如下：

![无人机系统演示](demo.gif)

## 技术栈

### 系统环境
- Java EE 8
- Servlet 3.0
- Apache Maven 3

### 主框架
- Spring Boot 2.2.x
- Spring Framework 5.2.x
- Apache Shiro 1.7

### 持久层
- Apache MyBatis 3.5.x
- Hibernate Validation 6.0.x
- Alibaba Druid 1.2.x

### 视图层
- Bootstrap 3.3.7
- Thymeleaf 3.0.x

### 数据库
- MySQL（生产环境）
- SQLite（开发环境，可选）

## 功能特性

- ✅ 无人机信息录入
- ✅ 无人机信息查询（支持模糊查询和条件过滤）
- ✅ 无人机信息修改
- ✅ 无人机信息删除
- ✅ 分页显示
- ✅ 请求拦截和日志记录

## 快速开始

### 1. 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+

### 2. 数据库初始化

```bash
# 使用MySQL客户端执行初始化脚本
mysql -u root -p < init-database.sql
```

或者手动执行：
1. 创建数据库：`drone_management`
2. 执行`init-database.sql`中的建表和初始数据脚本

### 3. 配置敏感信息

项目中所有敏感配置均使用占位符，**必须替换为你自己的值**后才能正常运行。

#### 方式一：通过环境变量配置（推荐）

设置以下环境变量，应用会自动读取：

| 环境变量 | 说明 | 示例 |
|---------|------|------|
| `DB_PASSWORD` | MySQL 数据库密码 | `your_db_password` |
| `MAIL_USERNAME` | QQ 邮箱地址 | `your_email@qq.com` |
| `MAIL_PASSWORD` | QQ 邮箱 SMTP 授权码 | `your_smtp_code` |
| `JWT_SECRET` | JWT 签名密钥（≥256位） | `your_jwt_secret_key_here` |
| `aes.secret-key` | AES 加密密钥（Base64编码） | `your_base64_aes_key` |

#### 方式二：直接修改配置文件

编辑 `src/main/resources/application-prod.yml`（生产环境）或 `application-dev.yml`（开发环境），将以下占位符替换为实际值：

```yaml
# 数据库密码
database:
  mysql:
    password: ${DB_PASSWORD:your_password}    # 替换 your_password

# 邮件配置
spring:
  mail:
    username: "${MAIL_USERNAME:your_qq_email@qq.com}"    # 替换为你的QQ邮箱
    password: "${MAIL_PASSWORD:your_qq_smtp_authorization_code}"    # 替换为QQ邮箱SMTP授权码

# JWT密钥
jwt:
  secret: "${JWT_SECRET:your_jwt_secret_key_at_least_256_bits}"    # 替换为你的密钥（至少256位）
```

编辑 `src/main/java/com/example/drone/config/AesConfig.java`，将 AES 密钥默认值替换：

```java
public AesUtil aesUtil(@Value("${aes.secret-key:your_base64_encoded_aes_key}") String base64Key) {
```

> **提示：** QQ 邮箱 SMTP 授权码获取方式：登录 QQ 邮箱 → 设置 → 账户 → POP3/SMTP 服务 → 开启并获取授权码。

### 4. 启动应用

**方式一：使用启动脚本（Windows）**
```bash
startup.bat
```

**方式二：使用Maven命令**
```bash
# 编译打包
mvn clean package -DskipTests

# 启动应用（MySQL）
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 5. 访问应用

打开浏览器访问：http://localhost:8080/drones

## 项目结构

```
src/main/java/com/example/drone/
├── config/              # 配置类（数据库、Shiro、Web）
├── controller/          # 控制器层
├── domain/              # 领域模型
│   ├── dto/            # 数据传输对象
│   ├── entity/         # 实体类
│   └── enums/          # 枚举类
├── exception/           # 异常处理
├── interceptor/         # 拦截器
├── repository/          # 数据访问层
│   ├── adapter/        # 数据库适配器
│   └── DroneRepository # Repository接口
├── security/            # 安全相关
└── service/             # 业务逻辑层
    └── impl/           # 业务实现

src/main/resources/
├── mapper/              # MyBatis映射文件
│   ├── mysql/          # MySQL映射
│   └── sqlite/         # SQLite映射
├── templates/           # Thymeleaf模板
└── application-*.yml    # 配置文件
```

## 数据库切换

系统支持MySQL和SQLite两种数据库，通过Spring Profile切换：

**使用MySQL（推荐）：**
```bash
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**使用SQLite（开发环境）：**
```bash
java -jar target/drone-management-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## 测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=DroneServiceImplTest
```

## 许可证

Copyright © 2024 无人机信息管理系统. All rights reserved.
