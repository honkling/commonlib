package me.honkling.commonlib.sql

import me.honkling.commonlib.sql.annotation.SQLType
import me.honkling.commonlib.sql.option.SQLOption
import java.sql.ResultSet
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.kotlinProperty

open class SQLObject(
    val resultSet: ResultSet
) {
    open fun save() {
        val fields = this::class.java.declaredFields.filter { it.name.endsWith("\$delegate") }
        val statement = StringBuilder("INSERT OR REPLACE INTO ${this::class.simpleName}")
        val values = mutableListOf<Any>()

        statement.append("(${fields.joinToString(", ") { getDelegateName(it) }}) VALUES(")

        for ((index, field) in fields.withIndex()) {
            field.isAccessible = true
            val instance = field[this] as SQLOption<*, *>
            val instanceClass = instance::class.java
            val getValue = instanceClass.getMethod("getValue", SQLObject::class.java, KProperty::class.java)
            val complex = getValue.invoke(instance, this, field.kotlinProperty)
            val serialize = instance::class.java.methods.find { it.name == "serializeToSQL" }!!
            val primitive = serialize.invoke(instance, complex)
            statement.append("?")

            if (index + 1 < fields.size)
                statement.append(", ")

            values += primitive
        }

        statement.append(");")
        executeSQL(statement.toString(), *values.toTypedArray())
    }
}