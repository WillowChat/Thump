import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.user.*
import org.gradle.api.NamedDomainObjectContainer
import java.io.File

val minecraftVersion by project
val forgeVersion by project
val mcpMappings by project
val thumpVersion by project
val warrenVersion by project
val kotlinVersion by project

buildscript {
    repositories {
        gradleScriptKotlin()
        maven { setUrl("http://files.minecraftforge.net/maven") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1-M02")
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
    gradleScriptKotlin()
    maven { setUrl("https://maven.hopper.bunnies.io/") }
}

dependencies {
    compile(kotlin("stdlib"))

    compile("engineer.carrot.warren.warren:Warren:$warrenVersion") {
        exclude(mapOf("group" to "org.slf4j"))
    }

    compile("org.slf4j:slf4j-api:1.7.21")

    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:2.2.9")
    testCompile("com.nhaarman:mockito-kotlin:0.10.0")
}

val buildNumberAddition = if(project.hasProperty("BUILD_NUMBER")) { ".${project.property("BUILD_NUMBER")}" } else { "" }

version = "$minecraftVersion-$thumpVersion$buildNumberAddition"
group = "engineer.carrot.warren.thump"
extra["archivesBaseName"] = "Thump"

minecraft {
    version = "$minecraftVersion-$forgeVersion"
    mappings = mcpMappings
    runDir = "run"
}

processResources {
    // Dummy task to be able to trigger manual token replacement

    val projectVersion = project.version as String
    val minecraftVersion = minecraftVersion as String

    inputs.property("version", projectVersion)
    inputs.property("version", minecraftVersion)
}

project.tasks.getByName("processResources").doLast {
    val projectVersion = project.version as String
    val minecraftVersion = minecraftVersion as String

    val processResourcesFiles = project.tasks.getByName("processResources").outputs.files.asFileTree

    processResourcesFiles.filter {
        val exists = it.exists()
        val directory = it.isDirectory()
        val isMcModInfo = it.name == "mcmod.info"

        exists && !directory && isMcModInfo
    }.forEach {
        var content = FileUtils.readFileToString(it)
        content = content.replace("\${version}", projectVersion)
        content = content.replace("\${minecraft_version}", minecraftVersion)
        FileUtils.writeStringToFile(it, content)
    }
}

shadowJar().relocate("engineer.carrot.warren.warren", "engineer.carrot.warren.thump.repack.warren")
shadowJar().relocate("engineer.carrot.warren.kale", "engineer.carrot.warren.thump.repack.kale")
shadowJar().relocate("org.slf4j", "engineer.carrot.warren.thump.slf4j")
shadowJar().relocate("com.squareup", "engineer.carrot.warren.thump.repack.com.squareup")
shadowJar().relocate("okio", "engineer.carrot.warren.thump.repack.com.squareup")
shadowJar().relocate("kotlin", "engineer.carrot.warren.thump.repack.kotlin")
shadowJar().relocate("org.jetbrains.annotations", "engineer.carrot.warren.thump.repack.annotations")
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

if (project.hasProperty("DEPLOY_DIR")) {
    configure<PublishingExtension> {
        mavenDeploy(this.repositories) { setUrl("file://${project.property("DEPLOY_DIR")}") }

        publications {
            it.create<MavenPublication>("mavenJava") {
                from(components.getByName("java"))

                artifact(deobfTask)
                artifact(sourcesTask)
            }
        }
    }
}

fun Project.minecraft(setup: UserBaseExtension.() -> Unit) = the<UserBaseExtension>().setup()
fun sourceSets(name: String) = (project.property("sourceSets") as SourceSetContainer).getByName(name)
fun Project.jar(setup: Jar.() -> Unit) = (project.tasks.getByName("jar") as Jar).setup()
fun Project.reobf(setup: TaskSingleReobf.() -> Unit) = (project.tasks.getByName(UserConstants.TASK_REOBF) as TaskSingleReobf).setup()
fun Project.processResources(setup: ProcessResources.() -> Unit) = (project.tasks.getByName("processResources") as ProcessResources).setup()
fun mavenDeploy(repositoryHandler: RepositoryHandler, configuration: MavenArtifactRepository.() -> Unit) =
        repositoryHandler.maven({ it.configuration() })
fun DependencyHandler.compile(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("compile", dependencyNotation) as ModuleDependency).setup()
fun shadowJar() = (project.tasks.findByName("shadowJar") as ShadowJar)

fun DependencyHandler.shade(dependencyNotation: Any) =
        add("shade", dependencyNotation)
fun DependencyHandler.shade(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("shade", dependencyNotation) as ModuleDependency).setup()

fun configuration(name: String) = project.configurations.getByName(name)
fun kotlin(module: String) = "org.jetbrains.kotlin:kotlin-$module:$kotlinVersion"