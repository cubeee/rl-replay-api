FROM openjdk:8u181-jdk AS BUILD_IMAGE
ENV APP_HOME=/home/gradle/project/
RUN mkdir -p $APP_HOME/src
WORKDIR $APP_HOME
COPY build.gradle gradlew $APP_HOME/
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
RUN chmod +x ./gradlew && ./gradlew jar

FROM openjdk:8u181-jre
WORKDIR /rl-replay-api/
COPY --from=BUILD_IMAGE /home/gradle/project/build/libs/rl-replay-api.jar .
CMD ["java", "-jar", "rl-replay-api.jar"]
