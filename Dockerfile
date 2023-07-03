FROM gradle AS TEMP_BUILD_IMAGE
WORKDIR /usr/app
COPY . .
RUN gradle bootJar

FROM openjdk:17
WORKDIR /usr/app
COPY --from=TEMP_BUILD_IMAGE /usr/app/build/libs/backend-0.0.1-SNAPSHOT.jar ./backend.jar

EXPOSE 8080

ENTRYPOINT exec java -jar ./backend.jar
