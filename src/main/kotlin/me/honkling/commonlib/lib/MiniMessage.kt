package me.honkling.commonlib.lib

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

val miniMessage = MiniMessage.miniMessage()

fun String.mm(vararg placeholders: TagResolver): Component {
    return miniMessage.deserialize(this, *placeholders)
}