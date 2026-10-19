pluginManagement {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "Architectury Plugin"
            url = uri("https://maven.architectury.dev/")
        }
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
//include("forge")
include("neoforge")

rootProject.name = "ShulkerBoxTooltip"
