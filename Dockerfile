FROM adoptopenjdk/openjdk11:alpine-jre

ADD target/challenge-money-transfer-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]