plugins {
    id("java")
}

group = "io.github.christechs.clayj"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc> {
    val options = options as StandardJavadocDocletOptions
    options.addStringOption("Xdoclint:none", "-quiet")
}