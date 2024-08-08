import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "me.honkling"
version = "0.1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))
    compileOnly("cc.ekblad:4koma:1.2.0")
}

kotlin {
    jvmToolchain(21)
}


publishing {
    publications {
        create<MavenPublication>("commonlib") {
            groupId = "me.honkling"
            artifactId = "commonlib"
            version = project.version.toString()

            from(components["kotlin"])
        }
    }
}
