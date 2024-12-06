package me.honkling.commonlib.lib

import me.honkling.commonlib.commonLib
import me.honkling.commonlib.pluginManager
import me.honkling.commonlib.scheduler
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import kotlin.reflect.KClass

private val contexts = mutableMapOf<String, SchedulerContext>()

class SchedulerContext(private val id: String?) {
    val listeners = mutableListOf<Listener>()
    private var ids = mutableListOf<Int>()

    inline fun <reified T : Event> subscribe(vararg events: KClass<out T>, crossinline task: SchedulerContext.(T) -> Unit) {
        val listener = object : Listener {} as Listener
        listeners += listener

        val executor = EventExecutor { _, event ->
            if (event !is T)
                return@EventExecutor

            task.invoke(this, event as T)
        }

        for (event in events)
            pluginManager.registerEvent(event.java, listener, EventPriority.NORMAL, executor, commonLib.plugin)
    }

    fun task(interval: Int, task: SchedulerContext.() -> Unit) {
        ids += scheduler.scheduleSyncRepeatingTask(commonLib.plugin, {
            task.invoke(this)
        }, 0L, interval.toLong())
    }

    fun resolve() {
        ids.forEach(scheduler::cancelTask)
        listeners.forEach(HandlerList::unregisterAll)

        if (id != null && id in contexts)
            contexts -= id
    }
}

fun scheduleTemporarily(id: String? = null, task: SchedulerContext.() -> Unit) {
    val context = SchedulerContext(id)
    task.invoke(context)

    if (id != null) {
        if (id in contexts)
            contexts[id]!!.resolve()

        contexts[id] = context
    }
}