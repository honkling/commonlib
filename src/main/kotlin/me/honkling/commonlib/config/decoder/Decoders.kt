package me.honkling.portal.config.decoder

import cc.ekblad.toml.configuration.TomlMapperConfigurator
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.transcoding.TomlDecoder
import cc.ekblad.toml.util.InternalAPI
import kotlin.enums.EnumEntries
import kotlin.reflect.KClass
import kotlin.reflect.KType

typealias Decoder = Pair<KClass<*>, TomlDecoder.(KType, TomlValue) -> Any?>

@OptIn(InternalAPI::class)
fun TomlMapperConfigurator.use(decoder: Decoder) {
    this.decoder(decoder.first, decoder.second)
}

class EnumDecoder<T : Any>(val clazz: KClass<T>) : (TomlDecoder, KType, TomlValue) -> Any? {
    init {
        if (!clazz.java.isEnum)
            throw IllegalArgumentException("Passed non-enum class to EnumDecoder (${clazz.java.name})")
    }

    override fun invoke(decoder: TomlDecoder, type: KType, value: TomlValue): Any? {
        val isKotlin = clazz.java.isAnnotationPresent(Metadata::class.java)

        if (value !is TomlValue.String)
            return value

        val entries: List<T> =
                if (isKotlin)
                    (clazz.java.getMethod("getEntries").invoke(null) as EnumEntries<*>).toList() as List<T>
                else (clazz.java.getMethod("values").invoke(null) as Array<T>).toList()

        val name = clazz.java.getMethod("name")
        val entry = entries.firstOrNull { (name.invoke(it) as String).lowercase() == value.value.lowercase() }
        return entry ?: value
    }

    fun get(): Decoder {
        return clazz to this
    }
}