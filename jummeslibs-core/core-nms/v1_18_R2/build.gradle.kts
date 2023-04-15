plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

description = "v1_18_R2"
version = rootProject.version


dependencies {
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}