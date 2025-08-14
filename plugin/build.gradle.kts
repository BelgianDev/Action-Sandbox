import java.net.URI

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
}

dependencies {
    implementation(project(":core"))
    implementation(gradleApi())
    implementation(localGroovy())
}

gradlePlugin {
    plugins {
        create("fresco") {
            id = "fr.atlasworld.fresco"
            implementationClass = project.property("plugin_main") as String
        }
    }
}

publishing {
    publications {
        withType<MavenPublication>().configureEach {
            if (name == "pluginMaven") {
                artifactId = "fresco-plugin"

                pom {
                    name = "Fresco Gradle Plugin"
                    description = "Fresco Gradle Plugin."
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
            }
        }
    }

    repositories {
        mavenLocal()

        maven {
            name = "AtlasWorld-Repository"
            isAllowInsecureProtocol = true

            val isSnapshot = System.getenv("RELEASE") != "true"
            var release = URI.create("http://repository.atlasworld.fr/repository/maven-releases/")
            var snapshot = URI.create("http://repository.atlasworld.fr/repository/maven-snapshots/")

            url = if (isSnapshot) {release} else {snapshot}

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
