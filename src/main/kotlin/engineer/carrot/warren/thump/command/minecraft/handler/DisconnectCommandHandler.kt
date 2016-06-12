package engineer.carrot.warren.thump.command.minecraft.handler

//class DisconnectCommandHandler(private val servicePlugins: IThumpServicePlugins) : ICommandHandler {
//
//    override val command: String
//        get() = COMMAND_NAME
//
//    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
//        if (parameters.size < 1) {
//            sender.addChatMessage(TextComponentString("Incorrect usage."))
//            sender.addChatMessage(TextComponentString(" Usage: " + this.usage))
//            return
//        }
//
//        val id = parameters[0]
//        val state = servicePlugins.wrappers[id]?.state
//        if (state == null) {
//            sender.addChatMessage(TextComponentString("That network ID doesn't exist."))
//            return
//        }
//
//        when (state) {
//            WrapperState.READY -> sender.addChatMessage(TextComponentString("That ID isn't running yet"))
//            else -> {
//                sender.addChatMessage(TextComponentString("Disconnecting network with id: " + id))
//                servicePlugins.stop(id, shouldReconnect = false)
//            }
//        }
//    }
//
//    override val usage: String
//        get() = COMMAND_NAME + " " + COMMAND_USAGE
//
//    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
//        if (parameters.size <= 1) {
//            val handlerId = parameters[0]
//            return Lists.newArrayList(Iterables.filter(
//                    servicePlugins.wrappers.keys,
//                    PredicateHelper.StartsWithPredicate(handlerId)))
//        }
//
//        return Lists.newArrayList()
//    }
//
//    companion object {
//
//        private val COMMAND_NAME = "disconnect"
//        private val COMMAND_USAGE = "<id>"
//    }
//}
