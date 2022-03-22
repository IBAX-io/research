FROM openjdk:17-jdk-alpine
VOLUME /tmp
ADD new-consensus-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=node21"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]