package com.youngerhousea.miraicompose.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.youngerhousea.miraicompose.utils.getAvatarImage
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.supervisorJob


interface ComposeBot : Bot {

    // Mutable delegete
    val avatar: ImageBitmap

    companion object {
        operator fun invoke(bot: Bot): ComposeBot = ComposeBotImpl(bot)

        val instances: MutableList<ComposeBot> by mutableStateOf(mutableListOf())
    }
}

private class ComposeBotImpl(bot: Bot) : Bot by bot, ComposeBot {

    private var _avatar by mutableStateOf(ImageBitmap(200, 200))

//    private val onlineListener: Listener<BotOnlineEvent> =
//        eventChannel.parentJob(supervisorJob).subscribeAlways(
//            priority = EventPriority.MONITOR,
//            concurrency = ConcurrencyKind.LOCKED
//        ) {
//            loadResource()
//        }

    override val avatar: ImageBitmap get() = _avatar

    init {
        ComposeBot.instances.add(this)
        supervisorJob.invokeOnCompletion {
            ComposeBot.instances.remove(this)
        }
        if (bot.isOnline)
            bot.launch {
                loadResource()
            }
    }

    private suspend fun loadResource() {
        _avatar = bot.getAvatarImage()
    }
}

