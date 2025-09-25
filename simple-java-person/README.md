# Optimisation avec JDK >= 24 (Ahead-of-Time Class Loading & Linking)

### Application Springboot REST + DB (MySQL ou H2) + Properties + Timer

Comparer les temps de startup selon les différentes configurations

- build + run en mode JVM dev spring-boot:run
- build + run en mode JVM avec un fat jar
- build + run en mode JVM avec un fat jar + Class Loading & Linking (feature jdk >= 24)
- build + run en mode container docker JVM avec buildpack
- build + run en mode container docker JVM + CDS + AOT avec buildpack
- build + run en mode container docker JVM + CDS + AOT + Class Loading & Linking
- build + run en mode natif avec GraalVM (necessite GraalVM installé)
- build + run en mode container docker natif buildpack

---

📌 Tableau récapitulatif des temps de démarrage

| Configuration                                    | Start Time                        | Taille du livrable |
|--------------------------------------------------|-----------------------------------|--------------------|
| JVM dev spring-boot:run                          | Started in 1.848 seconds          |                    |
| JVM avec un fat jar                              | Started in 2.562 seconds          |                    |
| JVM avec un fat jar + Class Loading & Linking    | Started in 1.656 seconds          |                    |
| docker JVM avec buildpack                        | 🐢 Started in 3.074 seconds       |                    |
| docker JVM + CDS + AOT avec buildpack            | Started in 1.63 seconds           |                    |
| docker JVM + CDS + AOT + Class Loading & Linking | ?                                 |                    |
| natif avec GraalVM                               | 🏃‍♂️‍➡️ Started in 0.236 seconds | 185 Mo             |
| docker natif avec buildpack                      | Started in 0.554 seconds          |                    |

---

Utilisation du profile h2 : __-Dspring.profiles.active=h2__

Utiliser cette url pour acceder à MySql depuis le container :

```properties
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/person
```
---

## Application Springboot en mode JVM dev

- Build de l'application
    ```shell
    mvn clean package
    ```

- Run de l'application
    ```shell
    mvn -Dspring.profiles.active=h2 spring-boot:run
    ```

## Application Springboot en mode JVM avec un fat jar

- Build de l'application
    ```shell
    mvn clean package
    ```

- Run de l'application
    ```shell
    java -Dspring.profiles.active=h2 -jar target/person-app-1.0.0-SNAPSHOT.jar
    ```

## Application Springboot en mode JVM avec un fat jar + Class Loading & Linking (feature jdk >= 24)

- Build de l'application
    ```shell
    mvn clean package
    ```

- Preparation du fichier de configuration AOT `person.aotconf`

  1️⃣ Démarrer l'app en mode __training__ pour construire le cache AOT
  
  ```shell
  java -XX:AOTMode=record -XX:AOTConfiguration=person.aotconf -Dspring.profiles.active=h2 -jar target/person-app-1.0.0-SNAPSHOT.jar
  ```
  
  2️⃣ Création du fichier de cache `person.aot`
  ```shell
  java -XX:AOTMode=create -XX:AOTConfiguration=person.aotconf -XX:AOTCache=person.aot -cp target/person-app-1.0.0-SNAPSHOT.jar
  ```

- Run de l'application
  ```shell
  java -XX:AOTCache=person.aot -Dspring.profiles.active=h2 -jar target/person-app-1.0.0-SNAPSHOT.jar
  ```

## Application Springboot en mode container docker JVM avec buildpack

- Dans la configuration du spring-boot-maven-plugin, ajouter les options suivantes
  - 
```xml
    <configuration>
        <image>
            <env>
              <BP_JVM_VERSION>21</BP_JVM_VERSION>
              <BPE_DELIM_JAVA_TOOL_OPTIONS xml:space="preserve"> </BPE_DELIM_JAVA_TOOL_OPTIONS>
              <BPE_APPEND_JAVA_TOOL_OPTIONS>-Dspring.profiles.active=h2</BPE_APPEND_JAVA_TOOL_OPTIONS>
            </env>
          <name>${project.artifactId}-jvm:${project.version}</name>
        </image>
    </configuration>
```

Commande permettant de construire l'image docker __JVM__ avec le plugin __spring-boot-maven-plugin__. Ce plugin utilise
buildpacks pour construire l'image.

- Build de l'application
  ```shell
  mvn clean -Dimage.suffix=jvm spring-boot:build-image
  ```

- Run de l'application
  ```shell
  docker run -i --rm -p 8080:8080 docker.io/library/person-app-jvm:1.0.0-SNAPSHOT
  ```

## Application Springboot en mode container docker JVM avec buildpack + CDS + AOT avec buildpack

Dans la configuration du spring-boot-maven-plugin, ajouter les options suivantes:

```xml
    <configuration>
        <image>
            <env>
                <BP_SPRING_AOT_ENABLED>true</BP_SPRING_AOT_ENABLED>
                <BP_JVM_CDS_ENABLED>true</BP_JVM_CDS_ENABLED>
            </env>
        </image>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>process-aot</goal>
            </goals>
        </execution>
    </executions>
```

- Build de l'application
  ```shell
  mvn clean -Dimage.suffix=jvm spring-boot:build-image
  ```

- Run de l'application
  ```shell
  docker run -i --rm -p 8080:8080 docker.io/library/person-app-jvm:1.0.0-SNAPSHOT
  ```

## Application Springboot en mode container docker JVM + CDS + AOT + Class Loading & Linking

La génération du fichier AOT Cache n'est pas possible actuellement avec buildpack


## Application Springboot en mode natif avec GraalVM (necessite GraalVM installé)

Il faut préalablement avoir installé GraalVM et le support natif pour Java. Pour cela, le plugin __native-maven-plugin__ est utilisé.

- Vérification avant le build :

  ```shell
  mvn -version
  ```

  ```log
  Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)
  Maven home: /Users/fredericmencier/Projects/apache-maven-3.9.6
  Java version: 21.0.8, vendor: Oracle Corporation, runtime: /Users/fredericmencier/.sdkman/candidates/java/21.0.8-graal
  Default locale: fr_FR, platform encoding: UTF-8
  OS name: "mac os x", version: "14.4.1", arch: "aarch64", family: "mac"
  ```

- Build de l'application
  ```shell
  mvn clean native:compile -Pnative
  ```
- Run de l'application
  ```shell
  ./target/person-app
  ```

## Application Springboot en mode container docker natif avec buildpack

Commande permettant de construire l'image docker __NATIVE__ avec le plugin __native-maven-plugin__. Ce plugin utilise buildpacks pour construire l'image.

- Build de l'application
  ```shell
  mvn -Dimage.suffix=native spring-boot:build-image -Pnative
  ```

- Run de l'application
  ```shell
  docker run -i --rm -p 8080:8080 docker.io/library/person-app-native:1.0.0-SNAPSHOT
  ```