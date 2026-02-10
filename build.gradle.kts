import java.nio.charset.StandardCharsets
import java.util.Base64

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")

    // https://github.com/yananhub/flying-gradle-plugin
    id("tech.yanand.maven-central-publish") version "1.3.0"
}

group = "dev.donutquine"
version = System.getProperty("RELEASE_VERSION") ?: System.getenv("RELEASE_VERSION") ?: project.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    // testLogging.showStandardStreams = true
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nulls-team/toml")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("toml")
                description.set("A library for parsing TOML file format")
                url.set("https://github.com/nulls-team/toml")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("danila-schelkov")
                        name.set("Danila Schelkov")
                        email.set("me@donutquine.dev")
                    }
                }

                scm {
                    url.set("https://github.com/nulls-team/toml")
                    connection.set("scm:git:https://github.com/nulls-team/toml.git")
                    developerConnection.set("scm:git:ssh://git@github.com/nulls-team/toml.git")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.key")?.toString(),
        findProperty("signing.password")?.toString()
    )
    sign(publishing.publications["mavenJava"])
}

mavenCentral {
    val username = findProperty("sonatype.username")?.toString() ?: System.getenv("SONATYPE_USERNAME")

    val password = findProperty("sonatype.password")?.toString() ?: System.getenv("SONATYPE_PASSWORD")

    val credentials = "$username:$password"
    authToken = Base64.getEncoder().encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))
}
