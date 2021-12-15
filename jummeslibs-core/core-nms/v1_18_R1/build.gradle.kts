plugins {
    id("io.papermc.paperweight.userdev") version "1.3.2"
}

description = "v1_18_R1"
version = rootProject.version


dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}