package me.honkling.commonlib.lib

import me.honkling.commonlib.commonLib
import me.honkling.commonlib.pluginManager
import me.honkling.commonlib.scheduler
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import kotlin.reflect.KClass

class SchedulerContext {
    private val listeners = mutableListOf<Listener>()
    private var ids = mutableListOf<Int>()

    fun <T : Event> subscribe(event: KClass<T>, task: SchedulerContext.(T) -> Unit) {
        val listener = object : Listener {} as Listener
        listeners += listener
        pluginManager.registerEvent(event.java, listener, EventPriority.NORMAL, { _, event ->
            task.invoke(this, event as T)
        }, commonLib.plugin)
    }

    fun task(interval: Int, task: SchedulerContext.() -> Unit) {
        ids += scheduler.scheduleSyncRepeatingTask(commonLib.plugin, {
            task.invoke(this)
        }, 0L, interval.toLong())
    }

    fun resolve() {
        ids.forEach(scheduler::cancelTask)
        listeners.forEach(HandlerList::unregisterAll)
    }
}

fun scheduleTemporarily(task: SchedulerContext.() -> Unit) {
    val context = SchedulerContext()
    task.invoke(context)
}