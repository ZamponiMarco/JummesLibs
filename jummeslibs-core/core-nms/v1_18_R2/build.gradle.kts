plugins {
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

description = "v1_18_R2"
version = rootProject.version


dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}