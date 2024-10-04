package me.honkling.commonlib.lib

private val camelCase = Regex("(^[a-z]+|[A-Z][a-z]*)")
private val pascalCase = Regex("([A-Z][a-z]*)")

enum class CaseType(
    regex: String,
    val separator: String,
    val tokenMapper: (String, Int) -> String
) {
    CamelCase("(^[a-z]+|[A-Z][a-z]*)", "", ::camelCaseToken),
    PascalCase("([A-Z][a-z]*)", "", ::properCaseToken),
    SnakeCase("[a-z]+(_|$)", "_", ::lowercaseToken),
    ProperCase("[A-Z][a-z]*( |$)", " ", ::properCaseToken);

    val regex = Regex(regex)

    fun tokenize(input: String): List<String> {
        return regex.findAll(input)
            .map { it.value }
            .toList()
    }

    fun joinTokens(vararg tokens: String): String {
        return joinTokens(tokens.toList())
    }

    fun joinTokens(tokens: List<String>): String {
        var index = 0
        return tokens.joinToString(separator) {
            val value = tokenMapper(it, index)
            index++
            value
        }
    }
}

fun String.convertCase(from: CaseType, to: CaseType): String {
    return to.joinTokens(from.tokenize(this))
}

@Suppress("UNUSED_PARAMETER")
private fun properCaseToken(token: String, index: Int): String {
    return token[0].uppercase() + token.substring(1).lowercase()
}

private fun camelCaseToken(token: String, index: Int): String {
    val first =
        if (index == 0) token[0].lowercase()
        else token[0].uppercase()

    return first + token.substring(1).lowercase()
}

@Suppress("UNUSED_PARAMETER")
private fun lowercaseToken(token: String, index: Int): String {
    return token.lowercase()
}