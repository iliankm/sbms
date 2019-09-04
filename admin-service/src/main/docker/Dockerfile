FROM hirokimatsumoto/alpine-openjdk-11

VOLUME /tmp

# Source code should be in the /usr/src/app folder
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

RUN apk --no-cache add bash

COPY run.sh run.sh
RUN chmod u+x run.sh

COPY admin-service.jar app.jar

EXPOSE 8080

CMD ["./run.sh"]
