package me.honkling.commonlib.sql.annotation

annotation class Flags(
    val primaryKey: Boolean = false,
    val unique: Boolean = false,
    val notNull: Boolean = true
)
