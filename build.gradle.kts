plugins {
    java
}

group = "de.hglabor"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    implementation("com.velocitypowered:velocity-api:1.1.4")
    annotationProcessor("com.velocitypowered:velocity-api:1.1.4")
}

java {
    val version = JavaVersion.VERSION_11
    sourceCompatibility = version
    targetCompatibility = version
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
