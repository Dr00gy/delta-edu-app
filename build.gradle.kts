plugins {
    id("java")
}

group = "org.edu_app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // app
    implementation("org.thymeleaf:thymeleaf:3.1.3.RELEASE")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11") // Logback implementation for SLF4J

    // testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Fix for Gradle 9.0 compatibility
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

