FROM amazoncorretto:21.0.2-alpine3.19

WORKDIR /server

COPY . .
EXPOSE 8080

#VOLUME ["~/.gradle/"]

ENTRYPOINT [ "/bin/sh", "run_server.sh"]