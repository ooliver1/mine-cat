plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    // generates plugin.yml for the plugin
    id("kr.entree.spigradle") version "1.2.4"
}

group = "xyz.minecat"
version = "1.0.0a"  // x-release-please-version

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    // is this how util.logging works for slf4j
    testImplementation("org.slf4j:slf4j-jdk14:1.7.36")

    // add websocket library for the client
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
}

spigot {
    name = "minecat"
    authors = listOf("ooliver1 - Oliver Wilkes")
    apiVersion = "1.13"
}
