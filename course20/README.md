# 用Flink实现实时风控引擎

## 启动Kafka

```
# 如果wget较慢的话，可以在浏览器中下载 kafka，链接为 https://archive.apache.org/dist/kafka/0.10.1.1/kafka_2.11-0.10.1.1.tgz 
wget https://archive.apache.org/dist/kafka/0.10.1.1/kafka_2.11-0.10.1.1.tgz

tar zxvf kafka_2.11-0.10.1.1.tgz
cd kafka_2.11-0.10.1.1
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup bin/kafka-server-start.sh config/server.properties &
```

## 创建请求和响应topic

```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic event-input --partition 1 --replication-factor 1
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic event-output --partition 1 --replication-factor 1
```


## 启动Flink服务

在Intellij中启动`com.alain898.course.realtimestreaming.course20.riskengine.FlinkRiskEngine`类即可。

## 往Kafka发送测试数据

在Intellij中启动`com.alain898.course.realtimestreaming.course20.riskengine.TestData`类即可。

