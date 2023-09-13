FROM ghcr.io/graalvm/jdk-community:17
COPY build/libs/*.jar matprint.jar
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENTRYPOINT ["java", "-jar", "matprint.jar"]