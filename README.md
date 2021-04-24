# Challenge Money Transfer   

RESTful API for money transfer between two accounts

## Requirements

Docker, Maven

## How to run

- Generate .jar 

```bash
mvn clean install -DskipTests=true
```
- Build

```bash
docker-compose up
```

## Test 
Navigate to http://localhost:8080/swagger-ui.html# 

- Run tests

```bash
mvn clean verify
```


