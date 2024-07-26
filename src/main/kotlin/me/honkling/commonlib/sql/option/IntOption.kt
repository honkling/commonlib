package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType

@SQLType("INTEGER")
class IntOption(field: String? = null) : SQLOption<Int, Int>(
    field
)