FROM openjdk:8u171-jdk-alpine3.8

VOLUME /tmp

# Source code should be in the /usr/src/app folder
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY run.sh run.sh

COPY ../../../target/admin-service.jar app.jar

RUN chmod u+x run.sh

EXPOSE 8080
CMD ["run.sh"]
