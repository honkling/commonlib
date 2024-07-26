package me.honkling.commonlib.sql

import me.honkling.commonlib.sql.annotation.Flags
import me.honkling.commonlib.sql.annotation.SQLType
import me.honkling.commonlib.commonLib
import java.lang.reflect.Field
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.kotlinProperty

private val file = commonLib.plugin.dataFolder.resolve("database.db")
private val connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")

// Used to connect to JDBC on plugin load
// so users don't experience a delay when
// they're the first to query the database.
fun nopSQL() {}

fun querySQL(query: String, vararg values: Any): ResultSet {
    return prepareSQL(query, values).executeQuery()
}

fun executeSQL(query: String, vararg values: Any) {
    prepareSQL(query, values).executeUpdate()
}

inline fun <reified T : Any> mapSQL(query: String, vararg values: Any): List<T> {
    val resultSet = querySQL(query, *values)
    return mapSQL(resultSet)
}

inline fun <reified T : Any> mapSQL(resultSet: ResultSet): List<T> {
    val clazz = T::class

    if (SQLObject::class.java.isAssignableFrom(clazz.java)) {
        val constructor = clazz.java.getConstructor(ResultSet::class.java)
        val objects = mutableListOf<T>()

        while (resultSet.next()) {
            val clone = resultSet.cloneRow()
            val instance = constructor.newInstance(clone) as T
            objects += instance
        }

        return objects
    }

    val constructor = clazz.primaryConstructor
        ?: throw IllegalArgumentException("Class must have a primary constructor.")

    val parameters = constructor.parameters
    val queried = mutableListOf<T>()

    while (resultSet.next()) {
        val args = mutableMapOf<KParameter, Any?>()
        parameters.forEachIndexed { index, param ->
            val paramName = param.name ?: throw IllegalArgumentException("Parameter name cannot be null.")
            val value = resultSet.getObject(paramName)
            args[param] = value
        }

        queried.add(constructor.callBy(args))
    }

    return queried
}

fun createTable(clazz: KClass<out SQLObject>) {
    val fields = clazz.java.declaredFields.filter { it.name.endsWith("\$delegate") }
    val statement = StringBuilder("CREATE TABLE IF NOT EXISTS ${clazz.simpleName} (")

    for ((index, field) in fields.withIndex()) {
        val flags = field.kotlinProperty!!.findAnnotation<Flags>() ?: Flags()
        val name = field.name.substring(0, field.name.length - "\$delegate".length)
        statement.append(name, " ")

        val type = field.type.annotations.find { it is SQLType } as SQLType?
            ?: throw IllegalArgumentException("Option ${field.type.simpleName} is missing SQLType annotation.")

        statement.append(type.type)

        if (flags.unique)
            statement.append(" UNIQUE")

        if (flags.primaryKey)
            statement.append(" PRIMARY KEY")

        if (flags.notNull)
            statement.append(" NOT NULL")

        if (index + 1 < fields.size)
            statement.append(",")
    }

    statement.append(");")
    executeSQL(statement.toString())
}

fun getDelegateName(field: Field): String {
    return field.name.substring(0, field.name.length - "\$delegate".length)
}

fun createResultSet(vararg values: Pair<String, Any?>): PseudoResultSet {
    return PseudoResultSet(values)
}

private fun prepareSQL(query: String, values: Array<out Any>): PreparedStatement {
    val statement = connection.prepareStatement(query)

    for ((index, value) in values.withIndex())
        statement.setObject(index + 1, value)

    return statement
}

fun disposeSQLConnection() {
    connection.close()
}

fun ResultSet.cloneRow(): PseudoResultSet {
    val pairs = mutableListOf<Pair<String, Any?>>()

    for (i in 1..metaData.columnCount) {
        val name = metaData.getColumnName(i)
        val value = getObject(name)

        pairs += name to value
    }

    return createResultSet(*pairs.toTypedArray())
}