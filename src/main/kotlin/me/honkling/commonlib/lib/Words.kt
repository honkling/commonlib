package me.honkling.commonlib.lib

private val camelCase = Regex("(^[a-z]+|[A-Z][a-z]*)")
private val pascalCase = Regex("([A-Z][a-z]*)")

enum class CaseType(
    test: String,
    match: String,
    val separator: String,
    val tokenMapper: (String, Int) -> String
) {
    Camel("^([a-z]+)([A-Z][a-z]*)*$", "(^[a-z]+|[A-Z][a-z]*)", "", ::camelCaseToken),
    Pascal("^([A-Z][a-z]*)+$", "([A-Z][a-z]*)", "", ::properCaseToken),
    Snake("^([a-z]+(_|$))+$", "[a-z]+(_|$)", "_", ::lowercaseToken),
    Kebab("^([a-z]+(-|$))+$", "[a-z]+(-|$)", "-", ::lowercaseToken),
    Proper("^([A-Za-z]+( |$))+$", "[A-Z][a-z]*( |$)", " ", ::properCaseToken);

    val testRegex = Regex(test)
    val regex = Regex(match)

    fun isValid(input: String): Boolean {
        return testRegex.matches(input)
    }

    fun tokenize(input: String): List<String> {
        return regex.find(input)!!.groupValues
            .let { it.subList(1, it.size) }
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

fun String.convertCase(to: CaseType): String {
    val case = CaseType.entries.find { it.isValid(this) }
        ?: throw IllegalStateException("Invalid case for input '$this'")

    return to.joinTokens(case.tokenize(this))
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
