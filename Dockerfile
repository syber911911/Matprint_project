FROM ghcr.io/graalvm/jdk-community:17
COPY build/libs/*.jar matprint.jar
ENTRYPOINT ["java", "-jar", "matprint.jar"]