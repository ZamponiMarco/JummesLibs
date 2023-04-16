pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "JummesLibs"
include(":jummeslibs-core")
include(":jummeslibs-core:core-plugin")
include(":jummeslibs-core:core-nms")
include("jummeslibs-core:core-nms:v1_19_R1")
include("jummeslibs-core:core-nms:v1_19_R2")
include("jummeslibs-core:core-nms:v1_19_R3")