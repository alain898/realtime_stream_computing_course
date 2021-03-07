# 使用Flink开发微服务

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
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic request --partition 1 --replication-factor 1
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic response --partition 1 --replication-factor 1
```


## 启动Flink服务

在Intellij中启动`com.alain898.course.realtimestreaming.course_bonus01.flink.FlinkService`类即可。

## 启动请求接收服务

在Intellij中启动`com.alain898.course.realtimestreaming.course_bonus01.netty.NettyRequestReceiver`类即可。

## 发送http请求

curl http://localhost:7071/event -X POST -d '{"request":"hello"}'

