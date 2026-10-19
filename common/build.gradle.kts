plugins {
    java
    `maven-publish`
    signing
    id("architectury-plugin")
    id("dev.architectury.loom-no-remap")
    id("com.gradleup.shadow")
}

val projectArchiveBaseName: String by project
val enabledPlatforms: String by project
val fabricLoaderVersion: String by project
val janksonVersion: String by project
val makePom: (Project) -> Action<MavenPom> by rootProject.extra
val clearMainSpecSources: (AbstractCopyTask) -> Unit by rootProject.extra

val moduleArtifactId = "$projectArchiveBaseName-common"

base {
    archivesName = moduleArtifactId
}

architectury {
    common(enabledPlatforms.split(","))
}

val shadowCommon by configurations.creating

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Nullable annotations.
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.jetbrains:annotations:23.0.0")

    // Jankson
    compileOnly("blue.endless:jankson:$janksonVersion")
    shadowCommon("blue.endless:jankson:$janksonVersion")
}

tasks.javadoc {
    exclude("com/misterpemodder/shulkerboxtooltip/impl/**")
    exclude("com/misterpemodder/shulkerboxtooltip/mixin/**")
    exclude("com/misterpemodder/shulkerboxtooltip/*.java")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
    from(tasks.javadoc)
}

tasks.shadowJar {
    dependsOn(tasks.jar)
    clearMainSpecSources(this)
    from(zipTree(tasks.jar.flatMap { it.archiveFile }))

    relocate("blue.endless.jankson", "${rootProject.group}.shulkerboxtooltip.shadowed.blue.endless.jankson")

    configurations = listOf(shadowCommon)
}

tasks.jar {
    archiveClassifier = "raw"
}

configurations {
    apiElements {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.shadowJar)
    }
    runtimeElements {
        outgoing.artifacts.clear()
        outgoing.artifact(tasks.shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            groupId = project.group.toString()
            artifactId = moduleArtifactId
            version = project.version.toString()

            artifact(tasks.shadowJar) {
                builtBy(tasks.shadowJar)
            }

            artifact(tasks.named("sourcesJar")) {
                builtBy(tasks.named("sourcesJar"))
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
        sign(publishing.publications["mavenCommon"])
    } else {
        println("${project.name}: skipping jar signing")
    }
}
