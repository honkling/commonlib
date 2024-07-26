package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.SQLObject
import java.sql.ResultSet
import kotlin.reflect.KProperty

open class SQLOption<Primitive, Complex>(
    val field: String? = null
) {
    protected lateinit var resultSet: ResultSet
    private var value: Complex? = null

    operator fun getValue(thisRef: SQLObject, prop: KProperty<*>): Complex {
        if (value == null) {
            this.resultSet = thisRef.resultSet
            value = deserializeFromSQL(thisRef.resultSet.getObject(field ?: prop.name) as Primitive)
        }

        return value!!
    }

    operator fun setValue(thisRef: SQLObject, prop: KProperty<*>, value: Complex) {
        this.value = value
    }

    open fun serializeToSQL(value: Complex): Primitive {
        return value as Primitive
    }

    open fun deserializeFromSQL(value: Primitive): Complex {
        return value as Complex
    }
}