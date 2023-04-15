plugins {
    id("java-library")
    id("maven-publish")
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    runtimeOnly (project(":jummeslibs-core:core-plugin"))
    runtimeOnly (project(":jummeslibs-core:core-nms:v1_18_R1", "reobf"))
    runtimeOnly (project(":jummeslibs-core:core-nms:v1_18_R2", "reobf"))
    runtimeOnly (project(":jummeslibs-core:core-nms:v1_19_R1", "reobf"))
    runtimeOnly (project(":jummeslibs-core:core-nms:v1_19_R2", "reobf"))
    runtimeOnly (project(":jummeslibs-core:core-nms:v1_19_R3", "reobf"))
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven("https://jitpack.io")
    }

    tasks {

        compileJava {
            dependsOn(clean)
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

    }
}

tasks {
    apply(plugin = "com.github.johnrengelman.shadow")

    shadowJar{
        archiveName = "${baseName}-${version}.${extension}"
    }
}

publishing {

    publications {
        create<MavenPublication>("shadow") {
            project.shadow.component(this)
            artifact(tasks.shadowJar) {
                classifier = null
            }
            groupId = "com.github.jummes"
            artifactId = rootProject.name
            version = rootProject.properties["version"] as String
        }
    }
}