## Flink SQL CDC 依赖的 jar 包

本工程是 Flink SQL CDC 依赖的 jar 包。使用 mvn package 构建出 target/course21-jars-1.0-SNAPSHOT.jar 即可。

我将 Flink SQL CDC 依赖的各种插件工具，统一地放在该工程的 pom.xml 里进行管理。
由于 Flink 插件使用了 SPI 机制，需要用到 META-INF/services 下的插件标识符，
为了避免不同插件冲突，maven 构建时需要使用 ServicesResourceTransformer 进行转化。
详情可以参见 https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/ ，
重点是 Transform table connector/format resources 这一小节。
对于以上问题，我已在 pom.xml 里进行了处理，只需要使用 mvn package 构建即可。
构建出的 target/course21-jars-1.0-SNAPSHOT.jar 拷贝到 course21/flink-sql-cdc/course21-jars-1.0-SNAPSHOT.jar 即可。
