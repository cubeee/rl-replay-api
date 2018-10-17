FROM gradle:jdk8-alpine AS BUILD_IMAGE
RUN wget https://github.com/tfausak/rattletrap/releases/download/6.0.1/rattletrap-6.0.1-linux \
    -O /tmp/rattletrap
ENV APP_HOME=/home/gradle/project/
RUN mkdir -p $APP_HOME/src
WORKDIR $APP_HOME
COPY build.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
RUN gradle jar

FROM openjdk:8-jre
WORKDIR /rl-replay-api/
COPY --from=BUILD_IMAGE /home/gradle/project/build/libs/rl-replay-api.jar .
COPY --from=BUILD_IMAGE /tmp/rattletrap .
RUN chmod +x /rl-replay-api/rattletrap
CMD ["java", "-jar", "rl-replay-api.jar"]
