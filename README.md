# Thump

## Introduction
Thump is a Minecraft IRC bridge that uses the [Warren](https://github.com/voxelcarrot/Warren) library.

It will provide an API for plugins so others can integrate their own functionality relatively easily.

Current features:
* Basic Minecraft -> IRC integration
 * Chat, server chat, /me, join, leave, death, achievement
 * Multiple rooms, multiple networks
* Basic IRC -> Minecraft integration
 * Chat
* Basic connection management
 * Connect, disconnect, status
 * Tab completion support for all sections of all commands, including networks

Check the [Issues](https://github.com/voxelcarrot/Thump/issues) for an idea of what needs doing before the next milestone is released (currently MVP).

Development builds are published at https://hopper.bunnies.io/job/Thump/. Documentation will be on the [wiki](https://github.com/voxelcarrot/Thump/wiki).

## Building
This mod uses Forge's Gradle wrapper for pretty easy setup and building. There are better guides around the internet for using it, and I don't do anything particularly special.

The general idea:
* **Setup**: `./gradlew [setupDevWorkspace/setupDecompWorkspace] [idea/eclipse]`
* **Building**: `./gradlew build`

If you run in to odd Gradle issues, doing `./gradlew clean` usually fixes it.

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).
