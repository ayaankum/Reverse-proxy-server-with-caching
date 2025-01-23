FROM openjdk:15-jdk-alpine

RUN apk update && \
    apk upgrade && \
    apk add git && \
    apk add wget && \
    apk add unzip

ENV THREADS = 4
ENV GRADLE_VERSION=6.5.1
ENV GRADLE_HOME=/opt/gradle
ENV GRADLE_FOLDER=$GRADLE_HOME
ENV GRADLE_USER_HOME=$GRADLE_HOME
ENV PATH=$GRADLE_HOME/bin:$PATH

RUN mkdir -p $GRADLE_USER_HOME && \
      chmod g+s $GRADLE_USER_HOME && \
      wget https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
      unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt && \
      mv /opt/gradle-${GRADLE_VERSION}/* $GRADLE_HOME && \
      rm -f gradle-${GRADLE_VERSION}-bin.zip

RUN cd /opt/ && \
    git clone https://github.com/antukhov/MaltaProxy.git && \
    cd ./MaltaProxy && \
    gradle wrapper && \
    ./gradlew build --debug

WORKDIR /opt/MaltaProxy/
RUN pwd
RUN ls -la
RUN mv ./build/libs/* ./build/libs/MaltaProxy.jar
ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/MaltaProxy/build/libs/MaltaProxy.jar SERVER_PORT=8081 THREADS=${THREADS}