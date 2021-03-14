# Kappa架构示例

## 启动Kafka

```
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup bin/kafka-server-start.sh config/server.properties &
```
## 启动MySQL

```
docker run -d --name mysql5.7 -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:5.7

docker exec -it mysql5.7 /bin/bash
mysql -u root -p123456
CREATE DATABASE kappa;
USE kappa;
CREATE TABLE table_counts(id VARCHAR(64), start BIGINT, end BIGINT, product VARCHAR(32), v_count INT, layer VARCHAR(32), PRIMARY KEY(id));
CREATE INDEX index_start ON table_counts (start DESC);
CREATE INDEX index_end ON table_counts (end DESC);
```


## 创建输入输出topic

```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic event-input --partition 1 --replication-factor 1
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic event-output --partition 1 --replication-factor 1
```

## 启动程序

BatchLayer

FastLayer


## 发送数据
KafkaSender

## 使用SQL查询

执行ServerLayer

```
ServerLayer输出：
timestamp: 1615692144448
SELECT product, sum(v_count) as s_count from
(
SELECT * FROM table_counts WHERE start=26928199 AND end=26928202 AND layer='batch'
UNION
SELECT * FROM table_counts WHERE start>=107712808 AND end<=107712809 AND layer='fast'
) as union_table GROUP BY product;
(product_0,40)
(product_1,44)
(product_2,45)
(product_3,32)
(product_4,33)


MySQL查询输出：
mysql> SELECT * FROM table_counts WHERE start=26928199 AND end=26928202 AND layer='batch';
+----------------------------------+----------+----------+-----------+---------+-------+
| id                               | start    | end      | product   | v_count | layer |
+----------------------------------+----------+----------+-----------+---------+-------+
| 3b33e86bfa718fa476b12294af967e6c | 26928199 | 26928202 | product_3 |      30 | batch |
| 5a43228f6e94aab6906a6e192b4fdffe | 26928199 | 26928202 | product_4 |      30 | batch |
| 6cf3873b45e45766023c34a2f60e5ca2 | 26928199 | 26928202 | product_0 |      37 | batch |
| 81aede2e81adf127143f656d66f3a375 | 26928199 | 26928202 | product_1 |      41 | batch |
| e11a3057090d768a52ff7dcd93613d84 | 26928199 | 26928202 | product_2 |      41 | batch |
+----------------------------------+----------+----------+-----------+---------+-------+
5 rows in set (0.00 sec)

mysql> SELECT * FROM table_counts WHERE start>=107712808 AND end<=107712809 AND layer='fast';
+----------------------------------+-----------+-----------+-----------+---------+-------+
| id                               | start     | end       | product   | v_count | layer |
+----------------------------------+-----------+-----------+-----------+---------+-------+
| 1e7bc9862a23b9debb33c9992b2932b8 | 107712808 | 107712809 | product_0 |       3 | fast  |
| 3636d8d869712fe90bc09622669fcf86 | 107712808 | 107712809 | product_4 |       3 | fast  |
| 5d507ae2325d4ee2535331db7f7a8d60 | 107712808 | 107712809 | product_1 |       3 | fast  |
| adcd15ecb13dd45a5e0f7349341e9cea | 107712808 | 107712809 | product_2 |       4 | fast  |
| fcd27deda3c899d394243a8cae44b2a2 | 107712808 | 107712809 | product_3 |       2 | fast  |
+----------------------------------+-----------+-----------+-----------+---------+-------+
5 rows in set (0.00 sec)

mysql> SELECT product, sum(v_count) as s_count from
    -> (
    -> SELECT * FROM table_counts WHERE start=26928199 AND end=26928202 AND layer='batch'
    -> UNION
    -> SELECT * FROM table_counts WHERE start>=107712808 AND end<=107712809 AND layer='fast'
    -> ) as union_table GROUP BY product;
+-----------+---------+
| product   | s_count |
+-----------+---------+
| product_0 |      40 |
| product_1 |      44 |
| product_2 |      45 |
| product_3 |      32 |
| product_4 |      33 |
+-----------+---------+
5 rows in set (0.00 sec)

```

