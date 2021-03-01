-- 注意！！！注意将下面两个 CREATE TABLE 定义中的 hostname 参数，修改为你自己的主机 IP 地址。
-- 也就是用 ifconfig 查询得到的 en0 或 eth0 地址。

-- 使用 Table/SQL API 方式实现 Flink CDC，需要用到 Flink SQL Client 客户端工具。
-- 在 Flink SQL Client 里执行以下 SQL。

-- 创建源数据库
CREATE TABLE sourceTable (
  id INT,
  name STRING,
  counts INT,
  description STRING
) WITH (
 'connector' = 'mysql-cdc',
-- 本地实验时，hostname 很有可能是由本地局域网（比如 wifi 网络）路由器分配的动态 IP，
-- 本地局域网重连时动态 IP有可能会发生变化，此时注意修改下 hostname 为新的 IP 地址即可。
 'hostname' = '192.168.1.7', -- 改我 IP ! 改我 IP !
 'port' = '3306',
 'username' = 'root',
 'password' = '123456',
 'database-name' = 'db001',
 'table-name' = 'table001'
);

-- 创建目标数据库
CREATE TABLE sinkTable (
  id INT,
  name STRING,
  counts INT
) WITH (
  'connector' = 'elasticsearch-7',
-- 本地实验时，hostname 很有可能是由本地局域网（比如 wifi 网络）路由器分配的动态 IP，
-- 本地局域网重连时动态 IP有可能会发生变化，此时注意修改下 hostname 为新的 IP 地址即可。
  'hosts' = 'http://192.168.1.7:9200', -- 改我 IP ! 改我 IP !
  'index' = 'table001',
-- 下面三个 sink.bulk-flush 参数都与批次处理有关。
-- 这里为了实验能够立即看到效果，将批次处理的值都设置得偏小。
-- 生产环境为了提高性能，可以将以下三个参数适当调大些。
  'sink.bulk-flush.max-actions' = '1',
  'sink.bulk-flush.max-size' = '1mb',
  'sink.bulk-flush.interval' = '1s'
);

-- 启动 Flink SQL CDC 作业
insert into sinkTable select id, name, counts from sourceTable;


-- 查看 Flink CDC 作业是否启动
-- 访问 http://127.0.0.1:8081/#/job/running 页面，如果看到有一个 RUNNING job，说明 Flink CDC 作业已启动

