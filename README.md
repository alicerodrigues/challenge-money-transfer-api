# Challenge Money Transfer   

RESTful API for money tranfer between two accounts

## Requirements

JVM (Oracle JDK, OpenJDK or GraalVM) 11+ 


## How to run

- Environment Variables 

```bash
set BUCKET_NAME="managed-services-login-files"
set GOOGLE_APPLICATION_CREDENTIALS=gcp-credentials.json
set HOST="10.151.50.124:8080"
set SCHEME="http"
set PROJECT_ID="ultra-compound-277613"
```
- Iniciar API

```bash
mvnw spring-boot:run
```


Documentação da API disponível em: [http://localhost:8081/swagger-ui.html#/](http://localhost:8081/swagger-ui.html#/)

Também pode ser vizualizada na raiz do projeto no arquivo swagger.json

## Empacotamento

Pode ser usado o MavenWrapper encontrado na raiz do projeto ou uma instalação local para gerar o artefato **.jar**.

```bash
mvnw clean package
```

## Build e Deploy na Cloud (Cloud Build)

Os testes, empacotamento e deploy são feitos usando o arquivo **cloudbuild.yaml**

 - **cloudbuild.yaml**: Arquivo com as configurações de cada step (teste, empacotamento, deploy).
 
Criar uma trigger do [Cloud Build / Triggers](https://console.cloud.google.com/cloud-build/triggers) que utilizará o arquivo **cloudbuild.yaml**.

Adicionar Substitution variable na trigger:
- _PROJECT_ID_DEPLOY : Id do projeto em que a imagem do container será criada (Container Registry)


##### Criação dos Workloads, Services e Configurations no Kubernetes (somente na primeira vez)

Conectar no Cluster

```bash
gcloud container clusters get-credentials prd-cluster-3 --region southamerica-east1 --project ultra-compound-277613
```


Criar Config-Map, Deployment e Service

```bash
kubectl apply -f config-map.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

## Testes 

```bash
mvnw clean verify
```
