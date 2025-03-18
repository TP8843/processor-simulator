plugins {
    java
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

application {
    mainClass.set("org.example.Main")
}

val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}