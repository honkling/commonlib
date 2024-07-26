package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType

@SQLType("TEXT")
class StringOption(field: String? = null) : SQLOption<String, String>(
    field
)