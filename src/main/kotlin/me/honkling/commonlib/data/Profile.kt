package me.honkling.commonlib.data

import me.honkling.commonlib.lib.scheduleTemporarily
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

private val profiles = mutableMapOf<Player, Profile>()
private val task = scheduleTemporarily {
    subscribe(PlayerQuitEvent::class) { event ->
        profiles.remove(event.player)
    }
}

var profileProvider: Class<out Profile> = Profile::class.java

open class Profile(@Suppress("unused") val player: Player)

val Player.profile
    get() = profiles.computeIfAbsent(this) {
        profileProvider.getConstructor(Player::class.java).newInstance(this)
    }