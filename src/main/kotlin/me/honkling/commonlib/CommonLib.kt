package me.honkling.commonlib

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val pluginManager = Bukkit.getPluginManager()
val scheduler = Bukkit.getScheduler()

lateinit var commonLib: CommonLib<*>

class CommonLib<T : JavaPlugin>(val plugin: T) {
    init {
        commonLib = this
    }
}