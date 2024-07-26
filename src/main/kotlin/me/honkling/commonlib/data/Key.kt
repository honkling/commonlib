package me.honkling.commonlib.data

import me.honkling.commonlib.commonLib
import me.honkling.commonlib.lib.camelToSnake
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import kotlin.reflect.KProperty

class Key<P, C : Any>(
    val type: PersistentDataType<P, C>,
    val default: C
) {
    private lateinit var key: NamespacedKey

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Key<P, C> {
        if (!this::key.isInitialized)
            key = NamespacedKey(commonLib.plugin.name.lowercase().replace(" ", "_"), property.name.camelToSnake())

        return this
    }

    operator fun get(holder: PersistentDataHolder): C {
        val container = holder.persistentDataContainer
        return container.get(key, type) ?: default
    }

    operator fun set(holder: PersistentDataHolder, value: C) {
        val container = holder.persistentDataContainer
        container.set(key, type, value)
    }

    operator fun contains(holder: PersistentDataHolder): Boolean {
        val container = holder.persistentDataContainer
        return container.has(key)
    }
}

operator fun <P, C : Any> PersistentDataHolder.get(key: Key<P, C>): C {
    return key[this]
}

operator fun <P, C : Any> PersistentDataHolder.set(key: Key<P, C>, value: C) {
    key[this] = value
}

operator fun PersistentDataHolder.contains(key: Key<*, *>): Boolean {
    return this in key
}