## Flink CDC 实验说明

我们有两种方式实现 Flink CDC。一种是使用 Table/SQL 方式，另外一种是使用 DataStream 方式。

### 使用 DataStream 方式

使用 DataStream 方式实现 Flink CDC 的代码，请参考`com.alain898.course.realtimestreaming.course21.flinkcdc.FlinkCdcDemo`类。

实验步骤如下：

```
1. 使用 docker 启动数据库，参考 flink-sql-cdc/步骤1.用docker启动数据库.md
2. 在 MySQL 中创建表，参考 flink-sql-cdc/步骤2.在MySQL中创建表.sql
3. 启动 com.alain898.course.realtimestreaming.course21.flinkcdc.FlinkCdcDemo 类的 main 函数
4. 查看 MySQL 往 Elasticsearch 同步数据的效果，参考 flink-sql-cdc/步骤5.查看MySQL往Elasticsearch同步数据的效果.md
```

### 使用 Table/SQL 方式

使用 Table/SQL 方式实现 Flink CDC 的相关资料，都在本项目的 flink-sql-cdc 目录下。
请参考 flink-sql-cdc 目录下的步骤 1 至步骤 5 完成实验。

