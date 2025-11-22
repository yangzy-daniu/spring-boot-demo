# 使用多阶段构建来减小镜像大小
FROM maven:3.8.6-openjdk-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 打包应用（跳过测试）
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-jdk-slim

# 安装时区数据（如果需要）
RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata && \
    rm -rf /var/lib/apt/lists/*

# 设置时区（可选，根据需要调整）
ENV TZ=Asia/Shanghai

# 创建非root用户运行应用（安全考虑）
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口（根据你的应用调整）
EXPOSE 8080

# JVM 参数优化（根据需要调整）
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=prod"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]