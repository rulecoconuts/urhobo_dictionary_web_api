echo Version 1020_KKKK;
export GRADLE_OPTS="-Xmx110m -Dorg.gradle.jvmargs='-Xmx400m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8'"
# stop and start the gradle daemon to fix "daemon process has disappeared exception"
/bin/sh gradlew --status
/bin/sh gradlew --stop
/bin/sh ./gradlew build --no-daemon -x test;
#env $(cat .env | grep '^[^#;]' | xargs) ./gradlew clean build --scan
ls -a /root;
java -Xmx400m -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -jar ./build/libs/*-SNAPSHOT.jar;