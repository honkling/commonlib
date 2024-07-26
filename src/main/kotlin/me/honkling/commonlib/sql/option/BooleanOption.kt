package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType

@SQLType("INTEGER")
class BooleanOption(field: String? = null) : SQLOption<Int, Boolean>(field) {
    override fun serializeToSQL(value: Boolean): Int {
        return if (value) 1 else 0
    }

    override fun deserializeFromSQL(value: Int): Boolean {
        return value == 1
    }
}