# storm例子

## 启动storm docker

请在 course16/docker 目录下执行以下命令：
```
docker-compose -f docker-storm.yml down
docker-compose -f docker-storm.yml up -d
```

## 提交storm topology到storm集群

注意修改下面命令中的 /Users/yuwu/svn/realtime_stream_computing_course/course16/target/course16-2.0.0.jar 为你自己的路径：
```
docker run --link nimbus:job_nimbus --net docker_default -it --rm -v /Users/yuwu/svn/realtime_stream_computing_course/course16/target/course16-2.0.0.jar:/topology.jar storm:2.0.0 storm jar /topology.jar com.alain898.course.realtimestreaming.course16.streams.WordCountExample topology_world_count
```

## 查看示例运行结果

```
docker exec -it supervisor /bin/bash
root@8898fd7ac7d0:/apache-storm-2.0.0# cd /logs/
root@8898fd7ac7d0:/logs# ls -al
total 48
drwxr-xr-x 1 storm storm  4096 Aug 25 13:33 .
drwxr-xr-x 1 root  root   4096 Aug 25 13:32 ..
-rw-r--r-- 1 storm storm     0 Aug 25 13:32 access-supervisor.log
-rw-r--r-- 1 storm storm     0 Aug 25 13:32 access-web-supervisor.log
-rw-r--r-- 1 storm storm    24 Aug 25 13:33 console.log
-rw-r--r-- 1 storm storm 30219 Aug 25 13:33 supervisor.log
-rw-r--r-- 1 storm storm     0 Aug 25 13:32 supervisor.log.metrics
drwxr-xr-x 3 storm storm  4096 Aug 25 13:33 workers-artifacts
root@8898fd7ac7d0:/logs# tail -f console.log
(pear, 10)
(mango, 8)
(banana, 10)
(orange, 7)
(apple, 4)
(mango, 9)
(mango, 10)
(mango, 11)
(mango, 12)
(banana, 11)
```


