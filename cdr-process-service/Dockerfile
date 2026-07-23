FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre
#Bu, daha küçük olan gerçek çalışma ortamıdır. Maven burada yoktur; yalnızca Java uygulamasını çalıştırmak için gereken JRE vardır.
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
