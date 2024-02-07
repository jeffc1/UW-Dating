// install Gradle jvm tasks
plugins {
    kotlin("jvm") version "1.9.21"
    id("application")
}

// product release info
group = "org.example"
version = "1.0-SNAPSHOT"

// where to find libraries?
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

// use newer version of JUnit
tasks.test {
    useJUnitPlatform()
}

// version of Java to use
kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "org.example.MainKt"
}