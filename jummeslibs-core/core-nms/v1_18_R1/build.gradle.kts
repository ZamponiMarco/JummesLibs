plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

description = "v1_18_R1"
version = rootProject.version


dependencies {
    paperweight.paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}