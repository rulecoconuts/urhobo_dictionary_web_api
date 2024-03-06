FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /server

COPY .. .
EXPOSE 8080

ENTRYPOINT [ "/bin/sh", "run_server.sh"]