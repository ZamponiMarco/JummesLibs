plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    `maven-publish`
    id("java")
}

dependencies {
    implementation(project(":jummeslibs-core:core-plugin"))
    implementation(project(":jummeslibs-core:core-nms:v1_17_R1"))
    implementation(project(":jummeslibs-core:core-nms:v1_18_R1"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    tasks {
        shadowJar {
        }

        compileJava {
            dependsOn(clean)
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        build {
            dependsOn(shadowJar)
        }

    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.jummes"
            artifactId = rootProject.name
            version = properties["version"] as String

            from(components["java"])
        }
    }
}