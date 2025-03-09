# 🛍️ Product Service

## 📌 Visão Geral
O **Product Service** é um microserviço baseado em **Spring Boot**, projetado para gerenciar a inclusão de produtos utilizando **AWS SQS**, **PostgreSQL** e **Amazon S3**. Ele suporta execução local e em **Docker**, integrando-se ao **LocalStack** para simular serviços da AWS em ambiente de desenvolvimento. O serviço utiliza **Java 21**, **Spring Boot**, e conta com **testes unitários** e documentação baseada em **OpenAPI** utilizando **Swagger**.

---

## 🚀 Arquitetura

O serviço segue a arquitetura de **microservices**, processando produtos de forma assíncrona via **SQS** e armazenando informações no **PostgreSQL** e **S3**.

### 🏗 Componentes

1. **Product API (Spring Boot - RESTful API)**
   - Cadastro e consulta de produtos via HTTP.
   - Geração de URLs pré-assinadas para upload de arquivos no **S3**.

2. **SQS Consumer (Listener de Produtos)**
   - Consome mensagens da fila **product-queue** e armazena os produtos no **PostgreSQL**.
   - Gerencia retentativas e falhas, enviando mensagens para a **DLQ** após tentativas excedidas.

3. **PostgreSQL (Banco Relacional)**
   - Mantém registros dos produtos para consultas e processamento de status.

4. **Amazon S3 (Armazenamento de Arquivos)**
   - Armazena arquivos associados aos produtos, como imagens e vídeos.

5. **LocalStack (Simulação AWS)**
   - Emula **SQS**, **PostgreSQL** e **S3** para desenvolvimento local.

---

## 📜 Estrutura Arquitetural

```plaintext
            ┌──────────────────────┐
            │  Cliente │
            └──────────▲───────────┘
                       │ HTTP Request (Cadastro/Consulta)
                       ▼
            ┌──────────────────────┐
            │    Product API       │
            │    (Spring Boot)     │
            └──────────▲───────────┘
                       │
               ┌───────┴───────┐
               │   PostgreSQL  │
               │ (Armazena Produtos) │
               └───────▲───────┘
                       │
        ┌──────────────┴──────────────┐
        │       Product Listener      │
        │   (Consumer SQS - Spring Boot) │
        │ Consome e grava no PostgreSQL │
        └───────────▲──────────────────┘
                    │
        ┌───────────┴───────────┐
        │        SQS            │
        │  (Fila de Produtos)   │
        └───────────┬───────────┘
                    │
        ┌───────────▼───────────┐
        │        DLQ            │
        │ (Fila de Erros)       │
        └───────────────────────┘
```

---

## 🎯 **Retentativas e DLQ (Dead Letter Queue)**

O sistema implementa **retentativas automáticas** via **SQS**, garantindo a confiabilidade do processamento de mensagens. Se uma mensagem falhar repetidamente após **5 tentativas**, ela é encaminhada para a **DLQ (Dead Letter Queue)** para análise posterior.

- **Retentativas automáticas**: Caso um erro ocorra durante o processamento da mensagem, o SQS automaticamente tentará reenviá-la até atingir o limite configurado.
- **DLQ (Dead Letter Queue)**: Se um erro persistir após todas as tentativas, a mensagem é movida para essa fila, permitindo a investigação e correção de problemas sem afetar o fluxo principal.

---

## 🔄 **Assincronicidade e Alta Performance**

Para lidar com um grande volume de mensagens, o **Product Service** utiliza **processamento assíncrono** com **Spring @Async** e **SQS batch processing**, garantindo:

- **Processamento paralelo de mensagens**: Permite lidar com milhares de produtos simultaneamente.
- **Execução eficiente**: A API responde rapidamente enquanto o processamento ocorre em background.
- **Menor latência**: Graças ao uso de filas, os processos não bloqueiam requisições.

---

## 🛠 Como Rodar o Serviço

#### 🏠 Rodando Localmente

Você pode rodar o serviço localmente com **LocalStack** simulando os serviços da AWS.

1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-repositorio/product-service.git
   cd product-service
   ```

2. Inicie o **LocalStack**:
   ```sh
   localstack start
   ```

3. Execute o **Product Service** localmente com Maven:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

---

### 🐳 **Rodando com Docker Compose**

Para facilitar a execução local, utilize **Docker Compose** para subir o **LocalStack** e rodar o **Product Service**.

1. Rode os serviços com Docker Compose:
   ```sh
   docker-compose up 
   ```

2. O serviço estará disponível em `http://localhost:8080`

---

---

## 📄 Endpoints da API

### 📦 Produtos (`/products`)
- `GET /products` → Listar produtos (com paginação, filtros e ordenação)
- `POST /products` → Criar um novo produto
- `GET /products/{id}` → Buscar um produto pelo ID
- `DELETE /products/{id}` → Excluir um produto
- `GET /products/processing/{id}` → Consultar status do processamento do produto

### 🗂️ Categorias (`/categories`)
- `GET /categories` → Listar todas as categorias

### 📁 Arquivos (`/assets`)
- `POST /assets/generate-presigned-urls` → Gerar URLs pré-assinadas para upload
- `POST /assets/confirm-upload/{productId}` → Confirmar upload e associar os arquivos ao produto
- `GET /assets/{productId}` → Listar arquivos vinculados a um produto

---

---


## 📡 **Testando a API**

📡 Para facilitar os testes existem  pré cadastrada algumas categorias

### ✅ Verificar se a API está rodando:
```sh
curl -X GET http://localhost:8080/health
```

### 📝 Fluxo Principal da API

1️⃣ **Criar um Produto**
```sh
curl -X POST "http://localhost:8080/products" -H "Content-Type: application/json" -d '{"name": "Smartphone", "description": "Celular moderno", "price": 999.99, "categoryIds": [1, 2], "attributes": [{"name": "RAM", "type": "TEXT", "value": "8GB"}]}'
```

2️⃣ **Gerar URLs pré-assinadas para upload de arquivos**
```sh
curl -X POST "http://localhost:8080/assets/generate-presigned-urls" -H "Content-Type: application/json" -d '{"productId": 1, "files": [{"type": "IMAGE", "fileExtension": "jpg", "fileName": "product-image"}, {"type": "VIDEO", "fileExtension": "mp4", "fileName": "demo-video"}]}'
```

3️⃣ **Fazer upload dos arquivos para as URLs retornadas**
```sh
curl -X PUT "URL_PRE_ASSINADA" --upload-file "caminho/do/arquivo.jpg"
```

4️⃣ **Confirmar upload e associar os arquivos ao produto**
```sh
curl -X POST "http://localhost:8080/assets/confirm-upload/1" -H "Content-Type: application/json" -d '[1, 2]'
```

5️⃣ **Buscar detalhes de um produto específico**
```sh
curl -X GET "http://localhost:8080/products/1"
```

---

---

🔎 Melhorias Futuras

Implementação de cache: Reduzir chamadas ao banco de dados para otimizar a performance.

Indexação no banco de dados: Melhorar a velocidade das consultas utilizando índices eficientes.

Melhoria na gestão de arquivos

Monitoramento e logging avançado: Incluir ferramentas como Prometheus e Grafana para métricas em tempo real.



## 🎯 **Conclusão**

O **Product Service** é um microserviço **event-driven**, que processa produtos de forma assíncrona usando **SQS**, **PostgreSQL**, e **S3**. Ele garante **alta disponibilidade**, **retentativas automáticas**, e **tratamento de falhas via DLQ**, tornando-se um serviço robusto para grandes volumes de processamento.

