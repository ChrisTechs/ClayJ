plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("com.gradleup.nmcp") version "0.0.8"
}

group = "io.github.christechs"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc> {
    val options = options as StandardJavadocDocletOptions
    options.addStringOption("Xdoclint:none", "-quiet")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("ClayJ")
                description.set("A high performance, zero dependency, UI layout library for Java.")
                url.set("https://github.com/christechs/clayj")

                licenses {
                    license {
                        name.set("zlib/libpng License")
                        url.set("https://opensource.org/licenses/Zlib")
                    }
                }
                developers {
                    developer {
                        id.set("christechs")
                        name.set("Christian Steenkamp")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/christechs/clayj.git")
                    developerConnection.set("scm:git:ssh://github.com/christechs/clayj.git")
                    url.set("https://github.com/christechs/clayj")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

nmcp {
    publish("mavenJava") {
        username = project.findProperty("centralUsername") as String?
        password = project.findProperty("centralPassword") as String?
        publicationType = "USER_MANAGED"
    }
}