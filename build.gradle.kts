plugins {
    id("java")
    id("org.springframework.boot") version("2.7.8")
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "org.edu_app"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web") // For web functionality
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.springframework.boot:spring-boot-starter-logging") // For Spring logging (should config logback)

    // Jakarta
    implementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    // Spring Boot dependencies BOM
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.8"))

    // Serialization
    implementation("com.google.code.gson:gson:2.12.1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // db
    implementation("org.postgresql:postgresql:42.7.5")
    // NOTE: Add once testing for springboot is required
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

// Our Main specification for running w/ Gradle (./gradlew bootRun)
tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    mainClass.set("org.edu_app.Main")
}