plugins {
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

description = "v1_19_R1"
version = rootProject.version


dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}