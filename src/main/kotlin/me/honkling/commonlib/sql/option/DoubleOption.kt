package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType

@SQLType("REAL")
class DoubleOption(field: String? = null) : SQLOption<Double, Double>(
    field
)