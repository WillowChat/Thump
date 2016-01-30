# Thump

## Introduction
Thump is a simple Minecraft IRC bridge that uses the [Warren](https://github.com/CarrotCodes/Warren) library.

It will provide an API for plugins so others can integrate their own functionality.

See the [Curse Forge](http://minecraft.curseforge.com/mc-mods/231124-thump) page for current features.

Check the [Issues](https://github.com/CarrotCodes/Thump/issues) for an idea of what needs doing before the next milestone is released.

Development builds are published at https://hopper.bunnies.io/job/Thump/. Documentation will be on the [wiki](https://github.com/CarrotCodes/Thump/wiki).

It's mostly written in Kotlin. Sometimes the mixture of Forge, Java and Kotlin doesn't work well (for example, logging) - in these cases, it's written in Java.

## Building
This mod uses Forge's Gradle wrapper for pretty easy setup and building. There are better guides around the internet for using it, and I don't do anything particularly special.

The general idea:
* **Setup**: `./gradlew clean [setupDevWorkspace/setupDecompWorkspace] [idea/eclipse]`
* **Building**: `./gradlew clean build`

If you run in to odd Gradle issues, doing `./gradlew clean` usually fixes it.

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).
