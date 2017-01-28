import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.user.*
import org.gradle.api.NamedDomainObjectContainer

val minecraftVersion by project
val forgeVersion by project
val mcpMappings by project
val thumpVersion by project
val warrenVersion by project
val kotlinVersion by project

val projectTitle = "Thump"

buildscript {
    val buildscriptKotlinVersion = "1.1-M04"

    repositories {
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
        gradleScriptKotlin()
        maven { setUrl("http://files.minecraftforge.net/maven") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$buildscriptKotlinVersion")
        classpath("net.minecraftforge.gradle:ForgeGradle:2.2-SNAPSHOT")
        classpath("com.github.jengelman.gradle.plugins:shadow:1.2.3")
    }
}

apply {
    plugin("kotlin")
    plugin("net.minecraftforge.gradle.forge")
    plugin("maven")
    plugin("maven-publish")
    plugin("com.github.johnrengelman.shadow")
}

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
    gradleScriptKotlin()
    maven { setUrl("https://maven.ci.carrot.codes") }
}

dependencies {
    compile(kotlin("stdlib"))

    compile("chat.willow.warren:Warren:$warrenVersion") {
        exclude(mapOf("group" to "org.slf4j"))
    }

    compile("org.slf4j:slf4j-api:1.7.21")

    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:2.2.9")
    testCompile("com.nhaarman:mockito-kotlin:0.10.0")
}

val buildNumberAddition = if(project.hasProperty("BUILD_NUMBER")) { ".${project.property("BUILD_NUMBER")}" } else { "" }

version = "$minecraftVersion-$thumpVersion$buildNumberAddition"
group = "chat.willow.thump"
project.setProperty("archivesBaseName", projectTitle)

val modVersion = "$minecraftVersion-$forgeVersion"

minecraft {
    version = modVersion
    mappings = mcpMappings as String
    runDir = "run"
    replace(mapOf("@VERSION@" to project.version))
}

processResources {
    val projectVersion = project.version as String
    val minecraftVersion = minecraftVersion as String

    inputs.property("version", projectVersion)
    inputs.property("mcversion", minecraftVersion)

    from(sourceSets("main").resources.srcDirs) {
        include("mcmod.info")

        expand(mapOf("version" to project.version, "minecraft_version" to minecraftVersion))
    }

    from(sourceSets("main").resources.srcDirs) {
        exclude("mcmod.info")
    }
}

shadowJar().relocate("chat.willow.warren", "chat.willow.thump.repack.warren")
shadowJar().relocate("chat.willow.kale", "chat.willow.thump.repack.kale")
shadowJar().relocate("org.slf4j", "chat.willow.thump.helper.slf4j")
shadowJar().relocate("com.squareup", "chat.willow.warren.thump.repack.com.squareup")
shadowJar().relocate("okio", "chat.willow.thump.repack.com.squareup")
shadowJar().relocate("kotlin", "chat.willow.thump.repack.kotlin")
shadowJar().relocate("org.jetbrains.annotations", "chat.willow.thump.repack.annotations")
shadowJar().classifier = ""

(project.extensions.findByName(UserConstants.EXT_REOBF) as NamedDomainObjectContainer<IReobfuscator>).create("shadowJar")

val deobfTask = task<Jar>("deobfJar") {
    from(sourceSets("main").output)
    classifier = "deobf"
}

val sourcesTask = task<Jar>("sourcesJar") {
    dependsOn("classes")

    from(sourceSets("main").allSource)
    classifier = "sources"
}

project.artifacts.add("archives", deobfTask)
project.artifacts.add("archives", sourcesTask)
project.artifacts.add("archives", project.tasks.getByName("shadowJar") as ShadowJar)

configure<PublishingExtension> {
    val deployUrl = if (project.hasProperty("DEPLOY_URL")) { project.property("DEPLOY_URL") } else { project.buildDir.absolutePath }
    this.repositories.maven({ setUrl("$deployUrl") })

    publications {
        create<MavenPublication>("mavenJava") {
            from(components.getByName("java"))

            artifact(deobfTask)
            artifact(sourcesTask)

            artifactId = projectTitle
        }
    }
}

fun minecraft() = the<UserBaseExtension>()
fun Project.minecraft(setup: UserBaseExtension.() -> Unit) = the<UserBaseExtension>().setup()
fun sourceSets(name: String) = (project.property("sourceSets") as SourceSetContainer).getByName(name)
fun Project.jar(setup: Jar.() -> Unit) = (project.tasks.getByName("jar") as Jar).setup()
fun Project.reobf(setup: TaskSingleReobf.() -> Unit) = (project.tasks.getByName(UserConstants.TASK_REOBF) as TaskSingleReobf).setup()
fun Project.processResources(setup: ProcessResources.() -> Unit) = (project.tasks.getByName("processResources") as ProcessResources).setup()
fun DependencyHandler.compile(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("compile", dependencyNotation) as ModuleDependency).setup()
fun shadowJar() = (project.tasks.findByName("shadowJar") as ShadowJar)

fun DependencyHandler.shade(dependencyNotation: Any) =
        add("shade", dependencyNotation)
fun DependencyHandler.shade(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("shade", dependencyNotation) as ModuleDependency).setup()

fun configuration(name: String) = project.configurations.getByName(name)
fun kotlin(module: String) = "org.jetbrains.kotlin:kotlin-$module:$kotlinVersion"