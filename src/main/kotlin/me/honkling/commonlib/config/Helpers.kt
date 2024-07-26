package me.honkling.commonlib.config

import cc.ekblad.toml.TomlMapper
import cc.ekblad.toml.decode
import cc.ekblad.toml.model.TomlValue
import me.honkling.commonlib.commonLib

inline fun <reified T : Any> getAndMapConfig(name: String, mapper: TomlMapper): T {
    val file = commonLib.plugin.dataFolder.resolve(name)

    if (!file.exists())
        commonLib.plugin.saveResource(name, true)

    return mapper.decode<T>(file.toPath())
}

fun <T> TomlValue.value(): T {
    return when (this) {
        is TomlValue.List -> elements
        is TomlValue.Map -> properties
        is TomlValue.Bool -> value
        is TomlValue.Double -> value
        is TomlValue.Integer -> value
        is TomlValue.LocalDate -> value
        is TomlValue.LocalDateTime -> value
        is TomlValue.LocalTime -> value
        is TomlValue.OffsetDateTime -> value
        is TomlValue.String -> value
    } as T
}