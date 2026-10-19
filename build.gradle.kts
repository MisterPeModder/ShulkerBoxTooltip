import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.5-SNAPSHOT"
    id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT" apply false
    id("com.gradleup.shadow") version "9.2.2" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
    `maven-publish`
    signing
}

val mcVersion: String by project
val projectGroup: String by project
val projectBaseVersion: String by project

architectury {
    minecraft = mcVersion
}

subprojects {
    pluginManager.apply("dev.architectury.loom-no-remap")

    plugins.withId("dev.architectury.loom-no-remap") {
        configure<LoomGradleExtensionAPI> {
            silentMojangMappingsLicense()
        }

        dependencies {
            "minecraft"("com.mojang:minecraft:$mcVersion")
        }
    }

    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            repositories {
                val env = System.getenv()
                if (env["MAVEN_USER"] != null && env["MAVEN_PASS"] != null) {
                    maven {
                        val repo = if (projectBaseVersion.contains("SNAPSHOT")) env["MAVEN_SNAPSHOT_REPO"] else env["MAVEN_REPO"]
                        if (repo.isNullOrEmpty()) {
                            println($$"env variable $MAVEN_REPO (or $MAVEN_SNAPSHOT_REPO) is not defined!")
                            url = uri("")
                        } else {
                            url = uri(repo)
                        }
                        credentials {
                            username = env["MAVEN_USER"]
                            password = env["MAVEN_PASS"]
                        }
                    }
                } else {
                    println("properties mavenUser and/or mavenPass are not defined!")
                }
            }
        }
    }

    // Exclude .asc (signature) files from checksum generation
    tasks.withType<AbstractPublishToMaven>().configureEach {
        val matcher = Regex("""^(.+\.asc)\.(md5|sha1|sha256|sha512)$""")
        doFirst {
            // Remove checksum files for .asc files before publishing
            temporaryDir.walk()
                .filter { matcher.containsMatchIn(it.name) }
                .forEach { it.delete() }
        }
    }
}

allprojects {
    group = projectGroup
    version = "$projectBaseVersion+$mcVersion"

    repositories {
        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "CurseForge"
            url = uri("https://minecraft.curseforge.com/api/maven")
        }
        maven {
            name = "Shedaniel Maven"
            url = uri("https://maven.shedaniel.me")
        }
        maven {
            name = "TerraformersMC"
            url = uri("https://maven.terraformersmc.com/releases")
        }
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    plugins.withId("java") {
        configure<JavaPluginExtension> {
            withSourcesJar()
        }
    }
}

// Gradle Build Scan Publishing
val buildScanExtension = extensions.findByName("buildScan")
if (buildScanExtension != null && System.getenv("GRADLE_TOS_AGREE") != null) {
    buildScanExtension.withGroovyBuilder {
        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
        setProperty("termsOfServiceAgree", System.getenv("GRADLE_TOS_AGREE"))
    }
}

// common publishing POM configuration
val makePom: (Project) -> Action<MavenPom> = { project ->
    Action {
        val gitLink = project.property("gitLink") as String

        name = project.property("pomName") as String
        description = project.description
        url = "https://github.com/$gitLink"

        developers {
            developer {
                id = "misterpemodder"
                name = "Yanis Guaye"
                email = "yguaye44@gmail.com"
            }
        }

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "repo"
            }
        }

        scm {
            url = "https://github.com/$gitLink"
            connection = "scm:git:git@github.com:$gitLink.git"
            developerConnection = "scm:git:git@github.com:$gitLink.git"
        }
    }
}
extra["makePom"] = makePom

val getReleaseType: () -> String = {
    when {
        projectBaseVersion.contains("alpha") -> "alpha"
        projectBaseVersion.contains("beta") -> "beta"
        else -> "stable"
    }
}
extra["getReleaseType"] = getReleaseType

val parseChangelog: (String) -> String = { path ->
    val file = rootProject.file(path)

    if (!file.exists())
        "(no changelog)"
    else
        file.useLines { lines ->
            lines.takeWhile { !it.startsWith("===") }.joinToString("") { "$it\n" }
        }
}
extra["parseChangelog"] = parseChangelog

val clearMainSpecSources: (AbstractCopyTask) -> Unit = { task ->
    val getMainSpec = AbstractCopyTask::class.java.getDeclaredMethod("getMainSpec").apply { isAccessible = true }
    (getMainSpec.invoke(task) as org.gradle.api.internal.file.copy.DefaultCopySpec).sourcePaths.clear()
}
extra["clearMainSpecSources"] = clearMainSpecSources
