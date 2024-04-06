FROM gradle:8.6-jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM azul/zulu-openjdk-alpine:21
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/FreshApp-1.0-SNAPSHOT-standalone.jar /app/FreshApp-1.0-SNAPSHOT-standalone.jar
ENTRYPOINT ["java","-Xms64m", "-Xmx256m", "-XX:+UseSerialGC", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport","-jar","/app/FreshApp-1.0-SNAPSHOT-standalone.jar"]
