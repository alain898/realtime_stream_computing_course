# Samza例子

## 参考资料
https://samza.apache.org/startup/quick-start/latest/samza.html


## 启动Kafka

```
# 如果wget较慢的话，可以在浏览器中下载 kafka，链接为 https://archive.apache.org/dist/kafka/0.10.1.1/kafka_2.11-0.10.1.1.tgz 
wget https://archive.apache.org/dist/kafka/0.10.1.1/kafka_2.11-0.10.1.1.tgz

tar zxvf kafka_2.11-0.10.1.1.tgz
cd kafka_2.11-0.10.1.1
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup bin/kafka-server-start.sh config/server.properties &
```

## 创建输入数据流topic

```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic sample-text --partition 1 --replication-factor 1
```

## 启动程序

在Intellij中配置程序启动参数
```
--config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=src/main/resources/word-count-example.properties
```
启动 WordCountExample 类 main 函数。

## 灌入测试数据

测试数据 sample-text.txt 在本工程的 doc 目录下，也就是 course18/doc/sample-text.txt

```
bin/kafka-console-producer.sh --topic sample-text --broker localhost:9092 < ./sample-text.txt
```

## 查看程序结果

```
bin/kafka-console-consumer.sh --topic word-count-output --zookeeper localhost:2181 --from-beginning
```
