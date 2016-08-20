import net.minecraftforge.gradle.user.TaskSingleReobf
import net.minecraftforge.gradle.user.UserBaseExtension
import net.minecraftforge.gradle.user.UserConstants
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

val minecraftVersion by project
val forgeVersion by project
val mcpMappings by project
val thumpVersion by project
val warrenVersion by project

buildscript {
    repositories {
        gradleScriptKotlin()
        maven { setUrl("http://files.minecraftforge.net/maven") }
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
        classpath("net.minecraftforge.gradle:ForgeGradle:2.2-SNAPSHOT")
    }
}

apply {
    plugin("kotlin")
    plugin("net.minecraftforge.gradle.forge")
    plugin("maven")
    plugin("maven-publish")
}

repositories {
    gradleScriptKotlin()
    maven { setUrl("https://maven.hopper.bunnies.io/") }
}

val shadeConfiguration = configurations.create("shade")
configurations.getByName("compile").extendsFrom(shadeConfiguration)

dependencies {
    shade(kotlinModule("stdlib"))

    shade("engineer.carrot.warren.warren:Warren:$warrenVersion") {
        exclude(mapOf("group" to "org.slf4j"))
    }

    compile("org.slf4j:slf4j-api:1.7.21")
}

val buildNumberAddition = if(project.hasProperty("BUILD_NUMBER")) { ".${project.property("BUILD_NUMBER")}" } else { "" }

version = "$minecraftVersion-$thumpVersion$buildNumberAddition"
group = "engineer.carrot.warren.thump"

minecraft {
    version = "$minecraftVersion-$forgeVersion"
    mappings = mcpMappings
    runDir = "run"
}

reobf {
    extraSrgLines += "PK: engineer/carrot/warren/warren engineer/carrot/warren/thump/repack/warren"
    extraSrgLines += "PK: engineer/carrot/warren/kale engineer/carrot/warren/thump/repack/kale"
    extraSrgLines += "PK: org/slf4j engineer/carrot/warren/thump/slf4j"
    extraSrgLines += "PK: com/squareup engineer/carrot/warren/thump/repack/com/squareup"
    extraSrgLines += "PK: okio engineer/carrot/warren/thump/repack/okio"
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

jar {
    project.configurations.getByName("shade").forEach {
        from(project.zipTree(it)) {
            exclude { it.name.contains("META-INF") }
        }
    }
}

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
fun Project.reobf(setup: TaskSingleReobf.() -> Unit) = (project.tasks.getByName(UserConstants.TASK_REOBF) as TaskSingleReobf).setup()
fun Project.jar(setup: Jar.() -> Unit) = (project.tasks.getByName("jar") as Jar).setup()
fun Project.processResources(setup: ProcessResources.() -> Unit) = (project.tasks.getByName("processResources") as ProcessResources).setup()
fun mavenDeploy(repositoryHandler: RepositoryHandler, configuration: MavenArtifactRepository.() -> Unit) =
        repositoryHandler.maven({ it.configuration() })
fun DependencyHandler.compile(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("compile", dependencyNotation) as ModuleDependency).setup()

fun DependencyHandler.shade(dependencyNotation: Any) =
        add("shade", dependencyNotation)
fun DependencyHandler.shade(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("shade", dependencyNotation) as ModuleDependency).setup()

fun configuration(name: String) = project.configurations.getByName(name)