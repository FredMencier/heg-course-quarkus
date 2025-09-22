# Optimisation avec JDK >= 24 : Ahead-of-Time Class Loading & Linking

- Application Springboot REST + DB (MySQL) + Properties + Timer

Comparer les temps de startup selon les différentes versions
- run en mode JVM dev spring-boot:run
- run en mode JVM avec un fat jar
- run en mode JVM avec un fat jar et AOT Class Loading & Linking

Utilisation du profile h2 : -Dspring.profiles.active=h2

## Run de l'application Springboot en mode JVM

```shell
mvn spring-boot:run
```

## Construire le fat jar Springboot

```shell
mvn clean package
```

-> Le jar se trouve dans le répertoire `target`

Run de l'application Springboot en mode JVM :

```shell
java -jar target/person-app-1.0.0-SNAPSHOT.jar
```

## Construction du cache AOT et utilisation

1️⃣ Démarrer l'app en mode training pour construire le cache AOT
```shell
java -XX:AOTMode=record -XX:AOTConfiguration=person.aotconf -jar target/person-app-1.0.0-SNAPSHOT.jar
```

2️⃣ Création du fichier de cache
```shell
java -XX:AOTMode=create -XX:AOTConfiguration=person.aotconf -XX:AOTCache=person.aot -cp target/person-app-1.0.0-SNAPSHOT.jar
```

3️⃣ Utilisation du cache AOT
```shell
java -XX:AOTCache=person.aot -jar target/person-app-1.0.0-SNAPSHOT.jar
```

# Optimisation en build natif : binaire native avec GraalVM et BuildPack

## Construire un binaire natif avec GraalVM

Il faut préalablement avoir installé GraalVM et le support natif pour Java.

```shell
mvn clean package -Pnative
```

## Construire un binaire natif encapsulé dans une image Docker

Utiliser cette url pour acceder à MySql depuis le container Springboot:

```properties
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/person
```

```shell
mvn spring-boot:build-image
```

Run de l'application Springboot en mode Natif avec Docker :

```shell
docker run -i --rm -p 8080:8080 person-app:1.0.0-SNAPSHOT
```