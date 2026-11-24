# A 股行情数据分析平台

基于 Spring Boot + ShardingSphere 的 A 股行情数据分析平台，包含行情采集、分片存储、查询展示和后台权限，不做交易。

仓库结构：

```text
stock_common   公共实体、Mapper、工具类、分片算法
stock_job      行情采集、XXL-JOB 执行器、线程池、MQ 生产
stock_backend  行情查询、缓存、认证授权、Excel 导出
frontend       Vue2 + ElementUI + webpack 前端
```

技术栈：Java 21、Spring Boot 3.1.5、Spring Security、JWT、MyBatis、MySQL、ShardingSphere-JDBC、Redis、Caffeine、RabbitMQ、XXL-JOB、Nginx、EasyExcel。

## 启动

### 1. 环境

- JDK 21
- Maven 3.9+
- MySQL 8
- Redis
- RabbitMQ
- XXL-JOB Admin

### 2. 数据库

准备以下数据库，表结构按实体和 Mapper 初始化：

```text
stock_sys_db
stock_db_2024
stock_db_2025
stock_db_2026
```

### 3. 配置

修改 `stock_job` 和 `stock_backend` 下的：

```text
src/main/resources/application.yml
src/main/resources/application-cache.yml
src/main/resources/application-mq.yml
src/main/resources/application-xxljob.yml
src/main/resources/sharding.yaml
```

把 `localhost`、`change_me` 和 XXL-JOB 地址改成你本地环境的值。  
JWT 密钥通过环境变量注入：

```bash
JWT_SECRET=your_32_char_secret
```

### 4. 打包启动

```bash
mvn clean package -DskipTests

java -jar stock_backend/target/stock_backend.jar
java -jar stock_job/target/stock_job.jar
```

端口：

```text
stock_backend   8091
stock_job       8092
XXL-JOB 执行器  6666
XXL-JOB Admin   8093
```

### 5. XXL-JOB

在 XXL-JOB Admin 中创建执行器：

```text
ming-stock-job-executor
```

配置四个任务：

```text
getInnerMarketInfo
getStockInfos
getStockBlockInfoTask
getOutMarket
```

### 6. 前端

前端在 `frontend/`，依赖较旧，建议用 Node 16：

```bash
cd frontend
npm install
npm run dev
```

开发服务默认跑在 80，`/api` 已代理到 `http://localhost:8091`。

打包：

```bash
npm run build
```

产物在 `frontend/dist`，复制到 Nginx 的 html 目录即可。

### 7. Nginx

前端构建后交给 Nginx，Nginx 托管 `dist` 并把 `/api` 转到后端：

```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location ^~ /api/ {
        proxy_pass http://127.0.0.1:8091;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```
