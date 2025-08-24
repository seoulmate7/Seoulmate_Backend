FROM openjdk:17-jdk

ENV TZ=Asia/Seoul

RUN microdnf -y update \
 && microdnf -y install tzdata \
 && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
 && echo 'Asia/Seoul' > /etc/timezone \
 && microdnf clean all

WORKDIR /app

COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

COPY app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["./wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "app.jar"]