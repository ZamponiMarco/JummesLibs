description = "core-nms"
version = rootProject.version

subprojects {
    dependencies {
        compileOnly(project(":jummeslibs-core:core-plugin"))
    }
}