FROM eclipse-temurin:17-jdk


# Refer to Maven build -> finalName
ARG JAR_FILE=target/rentstuff_api-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/rentstuff-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]