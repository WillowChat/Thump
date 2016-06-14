package engineer.carrot.warren.thump.plugin.irc.command

//class SendRawCommandHandler(private val manager: IThumpServicePlugins) : ICommandHandler {
//
//    override val command: String
//        get() = COMMAND_NAME
//
//    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
//        if (parameters.size < 2) {
//            sender.addChatMessage(TextComponentString("Incorrect usage."))
//            sender.addChatMessage(TextComponentString(" Usage: " + this.usage))
//            return
//        }
//
//        val id = parameters[0]
//        val wrapper = manager.wrappers[id]
//        val state = wrapper?.state
//        if (wrapper == null || state == null) {
//            sender.addChatMessage(TextComponentString("That network ID doesn't exist."))
//            return
//        }
//
//        when (state) {
//            WrapperState.RUNNING -> {
//                val line = parameters.drop(1).joinToString(separator = " ")
//                if (wrapper.sendRaw(line)) {
//                    sender.addChatMessage(TextComponentString("Sent message to $id: $line"))
//                } else {
//                    sender.addChatMessage(TextComponentString("Failed to send to $id: $line"))
//                }
//            }
//
//            else -> {
//                sender.addChatMessage(TextComponentString("Network $id isn't running - check with /thump status"))
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
//                    manager.wrappers.keys,
//                    PredicateHelper.StartsWithPredicate(handlerId)))
//        }
//
//        return Lists.newArrayList()
//    }
//
//    companion object {
//        private val COMMAND_NAME = "sendraw"
//        private val COMMAND_USAGE = "<id> <raw IRC line>"
//    }
//}
