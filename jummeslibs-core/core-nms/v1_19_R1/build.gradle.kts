plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

description = "v1_19_R1"
version = rootProject.version


dependencies {
    paperweight.paperDevBundle("1.19-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}