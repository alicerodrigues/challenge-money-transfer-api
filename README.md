# Challenge Money Transfer   

RESTful API for money tranfer between two accounts

## Requirements

JVM (Oracle JDK, OpenJDK or GraalVM) 11+ 
PostgresSql


## How to run

- Environment Variables 

```bash
set DATA_BASE=${DB_BASE}
set DB_HOST={DB_HOST}
set DB_PASS=${DB_PASS}
set DB_USER=${DB_USER}
```
- Run API

```bash
mvnw spring-boot:run
```


Documentation in: [http://localhost:8080/swagger-ui.html#/](http://localhost:8081/swagger-ui.html#/)


## Testes 

```bash
mvnw clean verify
```
