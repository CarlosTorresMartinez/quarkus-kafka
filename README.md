# Kafka + Schema Registry + Quarkus

Este proyecto utiliza Apache Kafka y Schema Registry con Avro para comunicar servicios desarrollados con Quarkus. A continuación se detallan los comandos y configuraciones necesarias para desplegar el entorno local con Docker y ejecutar la aplicación.

## 🐳 Requisitos

* Docker
* Docker Compose
* Java 17+
* Maven

## 🛠️ Despliegue con Docker Compose

Utiliza el siguiente archivo `docker-compose.yml` para levantar Zookeeper, Kafka y el Schema Registry:

```yaml
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    hostname: zookeeper
    container_name: kafka-docker-zookeeper-1
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    hostname: kafka
    container_name: kafka-docker-kafka-1
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

  schema-registry:
    image: confluentinc/cp-schema-registry:7.6.0
    hostname: schema-registry
    container_name: kafka-docker-schema-registry-1
    depends_on:
      - kafka
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: PLAINTEXT://kafka:29092
```

### 🚀 Comandos Docker

```bash
# Levantar el entorno
docker-compose up -d

# Verificar contenedores activos
docker ps

# Ver logs de Kafka
docker logs -f kafka-docker-kafka-1

# Ver logs del Schema Registry
docker logs -f kafka-docker-schema-registry-1

# Crear tópico manualmente (opcional si está habilitado auto-crear)
docker exec kafka-docker-kafka-1 kafka-topics --create \
  --topic persona-topic \
  --partitions 1 \
  --replication-factor 1 \
  --bootstrap-server localhost:29092

# Listar tópicos existentes
docker exec kafka-docker-kafka-1 kafka-topics --list \
  --bootstrap-server localhost:29092
```

## 🧪 Proyecto Quarkus

El proyecto Quarkus contiene:

* Serialización Avro con `confluent-kafka-avro-serializer`
* Deserialización Avro desde Kafka
* Comunicación mediante `smallrye-reactive-messaging-kafka`

### 🔧 Configuración en `application.properties`

```properties
# Producer
mp.messaging.outgoing.persona-producer.connector=smallrye-kafka
mp.messaging.outgoing.persona-producer.topic=persona-topic
mp.messaging.outgoing.persona-producer.bootstrap.servers=localhost:29092
mp.messaging.outgoing.persona-producer.value.serializer=org.apache.kafka.common.serialization.ByteArraySerializer

# Consumer
mp.messaging.incoming.persona-consumer.connector=smallrye-kafka
mp.messaging.incoming.persona-consumer.topic=persona-topic
mp.messaging.incoming.persona-consumer.group.id=code-with-quarkus
mp.messaging.incoming.persona-consumer.bootstrap.servers=localhost:29092
mp.messaging.incoming.persona-consumer.value.deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer

quarkus.log.level=INFO
quarkus.log.category."io.smallrye.reactive.messaging".level=DEBUG

```

### ▶️ Ejecutar Quarkus en modo dev

```bash
./mvnw quarkus:dev
```

## 📓 Notas

* El puerto `29092` es utilizado para comunicación interna entre contenedores.
* El puerto `9092` expone Kafka para el host local.
* El Schema Registry corre en `http://localhost:8081`.
* Asegúrate de compilar previamente los esquemas Avro si los defines en `src/main/resources/avro`.

---

Puedes ahora integrar este entorno con tu aplicación Quarkus utilizando `quarkus-messaging-kafka` y serializadores Avro para enviar y recibir mensajes estructurados.
