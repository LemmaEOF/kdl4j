import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    jacoco
    kotlin("jvm") version "1.4.30-M1"
}

group = "dev.hbeck.kdl"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.destination = file("${buildDir}/jacoco/coverage")
    }
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}