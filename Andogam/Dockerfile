# 1단계: Gradle로 빌드 (실행 이미지에는 빌드 도구가 필요 없으므로 분리)
FROM gradle:7.5-jdk11 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# 2단계: 실행 전용 경량 이미지
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
