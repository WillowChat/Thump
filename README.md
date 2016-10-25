# Thump

## Introduction
Thump is a simple, highly configurable Minecraft chat multiplexer with built in IRC support.

It uses [Warren](https://github.com/CarrotCodes/Warren) and [Kale](https://github.com/CarrotCodes/Kale), two IRCv3.2, unit tested IRC frameworks written in Kotlin.

See the [Curse Forge](http://minecraft.curseforge.com/mc-mods/231124-thump) page for current features.

Check the [Issues](https://github.com/CarrotCodes/Thump/issues) for an idea of what needs doing before the next milestone is released.

Development builds are published at https://hopper.bunnies.io/job/Thump/. Documentation will be on the [wiki](https://github.com/CarrotCodes/Thump/wiki).

It's mostly written in Kotlin. Sometimes the mixture of Forge, Java and Kotlin doesn't work well (for example, logging) - in these cases, it's written in Java.

## Why should you use this mod?

* It's geared towards admins setting up simple server links, with sensible configuration defaults
* If you don't like the defaults, there are configuration options for almost everything
* You can "live reload" the mod without restarting your server - change your configuration and do `/thump reload`
* [Warren](https://github.com/CarrotCodes/Warren) and [Kale](https://github.com/CarrotCodes/Kale) provide a great foundation for a stable IRC bridge

## Building
This mod uses Forge's Gradle wrapper for pretty easy setup and building. There are better guides around the internet for using it, and I don't do anything particularly special.

The general idea:
* **Setup**: `./gradlew clean [setupDevWorkspace/setupDecompWorkspace] [idea/eclipse]`
* **Building**: `./gradlew clean build`

If you run in to odd Gradle issues, doing `./gradlew clean` usually fixes it.

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).
