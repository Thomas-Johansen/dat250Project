plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "dat250.msd"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	//implementation("org.springframework.security:spring-security-test")
	implementation("com.google.code.gson:gson:2.9.0")
	implementation("com.squareup.okhttp3:okhttp:4.11.0")
	implementation("com.hivemq:hivemq-mqtt-client:1.3.3")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
