package me.honkling.commonlib.lib

private val camelCase = Regex("(^[a-z]+|[A-Z][a-z]*)")

fun String.camelToSnake(): String {
    return camelCase.findAll(this)
        .joinToString("_") { it.value.lowercase() }
}

fun String.camelToPascal(decapitalize: Boolean = false): String {
    fun map(input: String): String {
        val first = input[0].uppercase()
        val second = input.substring(1).let { if (decapitalize) it.lowercase() else it }
        return first + second
    }

    return camelCase.findAll(this)
        .map { it.value }
        .joinToString("", transform = ::map)
}