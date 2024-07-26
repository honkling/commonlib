package me.honkling.commonlib.sql.option

import me.honkling.commonlib.sql.annotation.SQLType
import java.time.Instant

@SQLType("INTEGER")
class InstantOption(field: String? = null) : SQLOption<Long, Instant>(
    field
) {
    override fun serializeToSQL(value: Instant): Long {
        return value.epochSecond
    }

    override fun deserializeFromSQL(value: Long): Instant {
        return Instant.ofEpochSecond(value)
    }
}