plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.thornedshields"
version = "1.0.0"

java {
    toolchain {
        // Verified from PaperMC's own build instructions for 26.2:
        // compiling/using paper-api 26.2 requires JDK 25.
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Verified against papermc.io/javadocs and the PaperMC/Paper GitHub repo:
    // 26.2 uses the version range below, not a fixed x.y.z-R0.1-SNAPSHOT string.
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
