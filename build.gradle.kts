plugins {
    id("java")
    id("org.springframework.boot") version("3.2.5")
    id("io.spring.dependency-management") version("1.1.4")
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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Jakarta, Hibernate and validators
    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.0")
    implementation("org.glassfish:jakarta.el:4.0.2")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    // Spring Boot dependencies BOM
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.4"))

    // Serialization
    implementation("com.google.code.gson:gson:2.12.1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Model
    implementation ("org.modelmapper:modelmapper:3.1.1")

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