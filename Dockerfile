FROM gradle:7.5.1-jdk17 as builder

WORKDIR /app

COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
COPY src src
COPY minifyJs.js ./

RUN ls -al

RUN gradle build

FROM eclipse-temurin:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]