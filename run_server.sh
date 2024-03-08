/bin/sh ./gradlew build;
java -XshowSettings:vm -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport $JAVA_OPTIONS -jar ./build/libs/*-SNAPSHOT.jar;