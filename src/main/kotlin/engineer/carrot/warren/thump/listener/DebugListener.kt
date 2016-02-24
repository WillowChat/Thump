package engineer.carrot.warren.thump.listener

import com.google.common.eventbus.Subscribe
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.warren.event.RawLineEvent

@Suppress("UNUSED", "UNUSED_PARAMETER")
class DebugListener() {

    @Subscribe
    fun onRawIRCLine(event: RawLineEvent) {
        LogHelper.info("Raw incoming line: {}", event.rawLine)
    }

}
