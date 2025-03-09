plugins {
    id("java")
    id("org.springframework.boot") version("2.7.8")
}

group = "org.edu_app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // app
    // thymeleaf
    implementation("org.thymeleaf:thymeleaf:3.1.3.RELEASE")
    // spring
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.8"))
    implementation("org.springframework.boot:spring-boot-starter")
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.12") // Logback implementation for SLF4J

    // testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // add once testing for springboot is required
//    testImplementation("org.springframework.boot:spring-boot-starter-test") {
//        exclude(mapOf("group" to "org.junit.vintage", "module" to "junit-vintage-engine"))
//    }
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Fix for Gradle 9.0 compatibility
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

