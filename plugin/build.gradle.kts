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
    }
}
