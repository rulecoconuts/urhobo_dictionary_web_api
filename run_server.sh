echo Version 1022_KKKK;
export GRADLE_OPTS="-Xmx150m -Dorg.gradle.jvmargs='-Xmx200m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8'"
# stop and start the gradle daemon to fix "daemon process has disappeared exception"
/bin/sh gradlew --status
/bin/sh gradlew --stop
/bin/sh ./gradlew build --no-daemon -x test;
#env $(cat .env | grep '^[^#;]' | xargs) ./gradlew clean build --scan
ls -a /root;
java -Xmx200m -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -jar ./build/libs/*-SNAPSHOT.jar;