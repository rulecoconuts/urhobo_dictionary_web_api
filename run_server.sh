/bin/sh ./gradlew build;
ls ./build/libs/
java -XshowSettings:vm -XX:+PrintFlagsFinal $JAVA_OPTIONS -jar ./build/libs/*-SNAPSHOT.jar;