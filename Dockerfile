FROM gradle:7.5.1-jdk17 as builder

# Install Node.js and npm
RUN apt-get update && apt-get install -y curl \
    && curl -sL https://deb.nodesource.com/setup_16.x | bash - \
    && apt-get install -y nodejs

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install  # Install Node.js dependencies

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