plugins {
  id("java")
}

group = "me.eeshe"
version = "1.0.0"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
}

tasks.jar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    destinationDirectory.set(file("output"))
}
