FROM openjdk:8u242-jre-slim
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ./kl-broadcast/target/kl-broadcast-1.0-SNAPSHOT.jar kl-broadcast.jar
COPY ./skywalking-agent/ ./skywalking-agent/
ENV SERVICE_NAME=""
ENV OAP_SERVICE=""
ENV JAVA_OPTS="\
-server \
-Xms512m \
-Xmx1024m \
-javaagent:./skywalking-agent/skywalking-agent.jar"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dskywalking.agent.service_name=$SERVICE_NAME -Dskywalking.collector.backend_service=$OAP_SERVICE -jar kl-broadcast.jar"]