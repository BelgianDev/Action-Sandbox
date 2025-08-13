plugins {
    id("java")
    id("idea")
    id("eclipse")
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "idea")
    apply(plugin = "eclipse")

    group = project.property("group") as String
    version = System.getenv("VERSION") ?: "undefined"

    repositories {
        mavenCentral()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(project.property("java_version") as String))

        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    idea {
        module {
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }

    eclipse {
        classpath {
            isDownloadSources = true
        }
    }
}
