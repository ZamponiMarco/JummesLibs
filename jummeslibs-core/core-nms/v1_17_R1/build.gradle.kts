description = "v1_17_R1"
version = rootProject.version

dependencies {
    implementation(files("./impl.jar"))
}

/*
plugins {
    id("io.papermc.paperweight.userdev") version "1.2.0"
}

description = "v1_18_R1"
version = rootProject.version


dependencies {
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}
 */
