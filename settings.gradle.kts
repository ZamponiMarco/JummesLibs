pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "jummeslibs"
include(":jummeslibs-core")
include(":jummeslibs-core:core-plugin")
include(":jummeslibs-core:core-nms")
include(":jummeslibs-core:core-nms:v1_17_R1")
include(":jummeslibs-core:core-nms:v1_18_R1")
