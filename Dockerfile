FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S carport && adduser -S carport -G carport
COPY --chown=carport:carport target/Carport-FOG.jar /app/app.jar
USER carport
EXPOSE 7070
ENTRYPOINT ["java", "-Xms32m", "-Xmx128m", "-XX:MaxMetaspaceSize=96m", "-XX:ReservedCodeCacheSize=32m", "-Xss512k", "-jar", "/app/app.jar"]
