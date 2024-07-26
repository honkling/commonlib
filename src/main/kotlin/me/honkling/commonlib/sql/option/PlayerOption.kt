package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

@SQLType("TEXT")
class PlayerOption(field: String? = null) : SQLOption<String, OfflinePlayer>(
    field
) {
    override fun serializeToSQL(value: OfflinePlayer): String {
        return value.uniqueId.toString()
    }

    override fun deserializeFromSQL(value: String): OfflinePlayer {
        return Bukkit.getOfflinePlayer(UUID.fromString(value))
    }
}