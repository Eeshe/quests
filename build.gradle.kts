plugins {
  id("java")
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.eeshe"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.extendedclip.com/releases/")
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
  compileOnly("me.clip:placeholderapi:2.12.2")

  implementation("org.mongodb:mongodb-driver-sync:5.6.4")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    destinationDirectory.set(file("output"))
}
