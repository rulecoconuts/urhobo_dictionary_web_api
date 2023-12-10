#env $(cat .env | grep '^[^#;]' | xargs) ./gradlew classes --warning-mode none
env $(cat .env | grep '^[^#;]' | xargs) ./gradlew generateTestJooq --warning-mode none