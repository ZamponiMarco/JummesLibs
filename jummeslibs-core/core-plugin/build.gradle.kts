plugins {
    `java-library`
    id ("io.freefair.lombok") version "6.3.0"
}

description = "core-plugin"
version = rootProject.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}