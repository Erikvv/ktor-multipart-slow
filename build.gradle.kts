plugins {
    kotlin("jvm") version "2.0.10"
    application
}

group = "com.zenmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-http-cio-jvm:3.0.0-rc-1")
}

tasks.test {
    useJUnitPlatform()
}