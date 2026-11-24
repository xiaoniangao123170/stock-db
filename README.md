# A 股行情数据分析平台

基于 Spring Boot + ShardingSphere 的 A 股行情数据分析平台，做行情采集、分片存储、查询展示和后台权限，不做交易。

> GitHub 描述：基于 Spring Boot + ShardingSphere 的 A 股行情数据分析平台（采集 / 分库分表 / 两级缓存 / 权限）

## 功能

- 大盘、外盘指数展示
- 板块排行、涨幅榜
- 涨停跌停家数、涨跌区间统计
- 成交量 T / T-1 对比
- 个股分时、日 K、周 K、主营业务
- 股票代码 / 名称搜索
- 榜单 Excel 导出
- 登录、验证码、JWT 认证、RBAC 权限

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 21、Spring Boot 3.1.5、Spring Security、JWT、MyBatis |
| 存储 | MySQL、ShardingSphere-JDBC 5.4.1 |
| 缓存 | Caffeine、Redis、SpringCache |
| 消息 | RabbitMQ |
| 调度 | XXL-JOB 2.3.1 |
| 前端 | Vue、ElementUI、axios、ECharts（由 Nginx 托管） |
| 部署 | Nginx、Docker |
| 报表 | EasyExcel |

## 模块

```text
stock_common   公共实体、Mapper、工具类、分片算法
stock_job      行情采集、XXL-JOB 执行器、线程池、MQ 生产
stock_backend  行情查询、缓存、认证授权、Excel 导出
```

## 数据设计

个股流水 `stock_rt_info` 按 `cur_time` 做年库、月表：

```text
ds-2024.stock_rt_info_202401 ~ 202412
ds-2025.stock_rt_info_202501 ~ 202512
ds-2026.stock_rt_info_202601 ~ 202612
```

- 大盘、板块、外盘按年分库，不按月分表；
- `stock_business` 股票字典做成广播表，每个数据源一份；
- 用户、角色、权限、日志在 `stock_sys_db`，不参与分片；
- 主键使用雪花算法，在应用侧生成。

分片键是 `cur_time`：

- 带时间的查询按年 / 月路由到单库单表；
- 跨年查询只路由相关年库，ShardingSphere 执行后归并；
- 只按 `stock_code` 不带时间会全路由，所以流水查询要求带时间条件。

## 采集与缓存

- XXL-JOB 拆分大盘、个股、板块、外盘四类任务；
- 个股按 15 只/批切分，线程池并发拉取、解析、批量入库；
- 新浪接口带 `Referer` 和 `User-Agent`，返回 JS 文本，正则解析；
- 仅大盘入库后发送 MQ 刷新缓存；
- 大盘使用 Caffeine L1 + Redis L2，MQ 失效并预热，TTL 兜底；
- 股票代码列表使用 Redis 单层缓存；
- 个股、板块直接查库，靠分片和索引。

## 认证与部署

- 验证码存 Redis，登录时校验；
- 登录成功后发 JWT，接口按权限码做方法级校验；
- Nginx 托管前端静态资源，并把 `/api` 反向代理到后端 8091；
- 页面和接口同源，跨域在部署层解决。

## 快速启动

### 1. 环境

- JDK 21
- Maven 3.9+
- MySQL 8
- Redis
- RabbitMQ
- XXL-JOB Admin

### 2. 初始化数据库

创建以下数据库并导入项目 SQL：

```text
stock_sys_db
stock_db_2024
stock_db_2025
stock_db_2026
```

### 3. 修改本地配置

`stock_job` 和 `stock_backend` 下的：

```text
src/main/resources/application.yml
src/main/resources/application-cache.yml
src/main/resources/application-mq.yml
src/main/resources/application-xxljob.yml
src/main/resources/sharding.yaml
```

把 `localhost`、`change_me`、XXL-JOB 地址改成你本地环境的值。  
**不要把真实密码、JWT 密钥提交到仓库。**

JWT 密钥通过环境变量注入：

```bash
JWT_SECRET=your_32_char_secret
```

### 4. 启动

```bash
mvn clean package -DskipTests

java -jar stock_backend/target/stock_backend.jar
java -jar stock_job/target/stock_job.jar
```

- `stock_backend`：8091
- `stock_job`：8092，XXL-JOB 执行器端口 6666
- XXL-JOB Admin：8093

在 XXL-JOB Admin 里创建执行器 `ming-stock-job-executor`，再配置四个任务：

```text
getInnerMarketInfo
getStockInfos
getStockBlockInfoTask
getOutMarket
```

### 5. Nginx

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

## 说明

这是一个课程 / 学习项目，重点是把采集、分库分表、缓存、认证和部署串起来。  
当前是单实例方案，没有做生产级高可用、完整压测和多实例缓存广播；这些在 README 里不包装成“已经上线”。
