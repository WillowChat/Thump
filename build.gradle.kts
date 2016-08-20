import net.minecraftforge.gradle.user.TaskSingleReobf
import net.minecraftforge.gradle.user.UserBaseExtension
import net.minecraftforge.gradle.user.UserConstants
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
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

version = "$minecraftVersion-$thumpVersion"
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

// FIXME: Expand is broken
task<ProcessResources>("userProcessResources") {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraftVersion)

    from(sourceSets("main").resources.srcDirs) {
        include { it.name == "mcmod.info" }
        expand(mutableMapOf("version" to project.version as String, "minecraft_version" to minecraftVersion as String))
    }

    from(sourceSets("main").resources.srcDirs) {
        exclude { it.name == "mcmod.info" }
    }
}

project.tasks.getByName("processResources").finalizedBy("userProcessResources")

jar {
    project.configurations.getByName("shade").forEach {
        from(project.zipTree(it)) {
            exclude { it.name.contains("META-INF") }
        }
    }
}

task<Jar>("deobfJar") {
    from(sourceSets("main").output)
    classifier = "deobf"
}

task<Jar>("sourcesJar") {
    dependsOn("classes")

    from(sourceSets("main").allSource)
    classifier = "sources"
}

// FIXME: Maven upload

fun Project.minecraft(setup: UserBaseExtension.() -> Unit) = the<UserBaseExtension>().setup()
fun sourceSets(name: String) = (project.property("sourceSets") as SourceSetContainer).getByName(name)
fun Project.reobf(setup: TaskSingleReobf.() -> Unit) = (project.tasks.getByName(UserConstants.TASK_REOBF) as TaskSingleReobf).setup()
fun Project.jar(setup: Jar.() -> Unit) = (project.tasks.getByName("jar") as Jar).setup()

fun DependencyHandler.compile(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("compile", dependencyNotation) as ModuleDependency).setup()

fun DependencyHandler.shade(dependencyNotation: Any) =
        add("shade", dependencyNotation)
fun DependencyHandler.shade(dependencyNotation: Any, setup: ModuleDependency.() -> Unit) =
        (add("shade", dependencyNotation) as ModuleDependency).setup()

fun configuration(name: String) = project.configurations.getByName(name)