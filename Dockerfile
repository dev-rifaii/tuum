FROM eclipse-temurin:21 as build

WORKDIR /server
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21 as finalize

COPY --from=build /server/build/libs/tuum.jar tuum.jar

ENTRYPOINT ["java", "-jar", "tuum.jar"]
