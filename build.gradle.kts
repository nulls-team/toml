plugins {
    id("java")
    id("maven-publish")
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


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nulls-team/toml")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getProperty("GITHUB_USERNAME") ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getProperty("GITHUB_TOKEN") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
