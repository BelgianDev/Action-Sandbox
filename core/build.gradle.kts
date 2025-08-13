import java.net.URI

plugins {
    id("java-library")
    id("maven-publish")
}

java {
    withJavadocJar()
}

dependencies {
    api(libs.annotations)
    api(libs.gson)

    implementation(libs.slf4j)
}

publishing {
    publications {
        create<MavenPublication>("core") {
            artifactId = "fresco-core"

            pom {
                name = "Fresco Core"
                description = "Core library for Fresco"
                url = "https://github.com/AtlasWorldMC/Fresco"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://github.com/AtlasWorldMC/Fresco/blob/master/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "raftdev"
                        name = "RaftDev"
                        email = "theraft08@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/AtlasWorldMC/Fresco.git"
                    developerConnection = "scm:git:ssh://github.com/AtlasWorldMC/Fresco.git"
                    url = "https://github.com/AtlasWorldMC/Fresco"
                }
            }

            from(components["java"])
        }
    }

    repositories {
        mavenLocal()

        maven {
            name = "AtlasWorld-Repository"

            val isSnapshot = System.getenv("RELEASE") != "true"
            var release = URI.create("https://repository.atlasworld.fr/repository/maven-releases/")
            var snapshot = URI.create("https://repository.atlasworld.fr/repository/maven-snapshots/")

            url = if (isSnapshot) {snapshot} else {release}

            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }

            metadataSources {
                gradleMetadata()
            }
        }
    }
}
