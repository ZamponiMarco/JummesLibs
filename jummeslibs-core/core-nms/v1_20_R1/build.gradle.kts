plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

description = "v1_20_R1"
version = rootProject.version


dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}