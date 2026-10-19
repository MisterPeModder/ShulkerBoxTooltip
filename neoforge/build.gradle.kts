import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.architectury.loom-no-remap")
    java
    `maven-publish`
    signing
    id("architectury-plugin")
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

val projectArchiveBaseName: String by project
val projectBaseVersion: String by project
val mcVersionFull: String by project
val neoForgeVersion: String by project
val janksonVersion: String by project
val hostGameVersions: String by project
val makePom: (Project) -> Action<MavenPom> by rootProject.extra
val getReleaseType: () -> String by rootProject.extra
val parseChangelog: (String) -> String by rootProject.extra
val clearMainSpecSources: (AbstractCopyTask) -> Unit by rootProject.extra

val moduleArtifactId = "$projectArchiveBaseName-neoforge"

base {
    archivesName = moduleArtifactId
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    runs {
        configureEach {
            // NeoForge's @OnlyIn warning makes no sense for cross-platform mods
            vmArg("-Dneoforge.warnings.onlyin.hide=true")
        }
    }
}

val common by configurations.creating
val shadowCommon by configurations.creating
configurations {
    // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
    compileClasspath { extendsFrom(common) }
    runtimeClasspath { extendsFrom(common) }
    named("developmentNeoForge") { extendsFrom(common) }
}

dependencies {
    "neoForge"("net.neoforged:neoforge:$neoForgeVersion")

    common(project(path = ":common")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) { isTransitive = false }

    // Nullable annotations.
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.jetbrains:annotations:23.0.0")

    // Jankson
    "localRuntime"("blue.endless:jankson:$janksonVersion")
    "forgeRuntimeLibrary"("blue.endless:jankson:$janksonVersion")
    shadowCommon("blue.endless:jankson:$janksonVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    dependsOn(tasks.jar)
    clearMainSpecSources(this)
    from(zipTree(tasks.jar.flatMap { it.archiveFile }))

    exclude("fabric.mod.json")
    exclude("architectury.common.json")

    relocate("blue.endless.jankson", "${rootProject.group}.shulkerboxtooltip.shadowed.blue.endless.jankson")

    configurations = listOf(shadowCommon)
    archiveClassifier = null
}

tasks.jar {
    archiveClassifier = "raw"
}

tasks.named<Jar>("sourcesJar") {
    val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.flatMap { it.archiveFile }.map { zipTree(it) })
}

// Include both common and neoforge documentation in the javadoc
val javadocProjects = listOf(":neoforge", ":common")
tasks.javadoc {
    setSource(javadocProjects.map { project(it).sourceSets.main.get().allJava })
    classpath = files(javadocProjects.map { project(it).sourceSets.main.get().compileClasspath })

    exclude("com/misterpemodder/shulkerboxtooltip/impl/**")
    exclude("com/misterpemodder/shulkerboxtooltip/mixin/**")
    exclude("com/misterpemodder/shulkerboxtooltip/*.java")
    exclude("com/misterpemodder/shulkerboxtooltip/neoforge/*.java")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
    from(tasks.javadoc)
}

(components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("mavenNeoForge") {
            groupId = project.group.toString()
            artifactId = moduleArtifactId
            version = project.version.toString()

            artifact(tasks.shadowJar) {
                builtBy(tasks.shadowJar)
            }

            artifact(tasks.named("sourcesJar")) {
                builtBy(tasks.named("sourcesJar"))
            }

            artifact(javadocJar) {
                builtBy(javadocJar)
            }

            pom(makePom(project))
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenNeoForge"])
    } else {
        println("${project.name}: skipping jar signing")
    }
}

publishMods {
    file = tasks.shadowJar.flatMap { it.archiveFile }
    displayName = "[NeoForge $mcVersionFull] v${project.version}"
    version = "${project.version}-neoforge"
    type = ReleaseType.of(getReleaseType())
    changelog = parseChangelog("changelog.txt")
    modLoaders.add("neoforge")
    dryRun = projectBaseVersion.contains("SNAPSHOT")

    if (!System.getenv("CURSE_API_KEY").isNullOrEmpty() && project.hasProperty("curseProjectId")) {
        curseforge {
            accessToken = providers.environmentVariable("CURSE_API_KEY")
            projectId = project.property("curseProjectId") as String
            minecraftVersions.addAll(hostGameVersions.split(","))
            javaVersions.add(JavaVersion.VERSION_25)
            client = true
            server = true
        }
    }

    if (!System.getenv("MODRINTH_TOKEN").isNullOrEmpty() && project.hasProperty("modrinthProjectId")) {
        modrinth {
            accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            projectId = project.property("modrinthProjectId") as String
            minecraftVersions.addAll(hostGameVersions.split(","))
        }
    }
}
