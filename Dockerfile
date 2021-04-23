FROM openjdk

WORKDIR /usr/src/myapp

COPY /target/challenge-money-transfer-api-*.jar /usr/src/myapp/app.jar

RUN chmod +x /usr/src/myapp/app.jar

ENTRYPOINT ["java"]

CMD ["-jar", "app.jar"] 