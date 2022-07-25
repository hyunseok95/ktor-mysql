package com.example.lib.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.OracleDialect
import org.jetbrains.exposed.sql.vendors.SQLiteDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.sql.ResultSet
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class TimeStampType : ColumnType(), IDateColumnType {

    val SQLITE_AND_ORACLE_DATE_TIME_STRING_FORMATTER by lazy {
        DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss.SSS",
            Locale.ROOT
        ).withZone(ZoneId.systemDefault())
    }

    val DEFAULT_DATE_TIME_STRING_FORMATTER by lazy {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(Locale.ROOT).withZone(ZoneId.systemDefault())
    }

    override val hasTimePart: Boolean = true
    override fun sqlType(): String = "TIMESTAMP"

    override fun nonNullValueToString(value: Any): String {
        val instant = when (value) {
            is String -> return value
            is Instant -> value
            is java.sql.Timestamp -> value.toInstant()
            else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
        }

        return when (currentDialect) {
            is OracleDialect -> "'${SQLITE_AND_ORACLE_DATE_TIME_STRING_FORMATTER.format(instant)}'"
            else -> "'${DEFAULT_DATE_TIME_STRING_FORMATTER.format(instant)}'"
        }
    }

    override fun valueFromDB(value: Any): Instant = when (value) {
        is java.sql.Timestamp -> value.toInstant()
        is String -> Instant.parse(value)
        else -> valueFromDB(value.toString())
    }

    override fun readObject(rs: ResultSet, index: Int): Any? {
        return rs.getTimestamp(index)
    }

    override fun notNullValueToDB(value: Any): Any = when {
        value is Instant && currentDialect is SQLiteDialect ->
            SQLITE_AND_ORACLE_DATE_TIME_STRING_FORMATTER.format(value)
        value is Instant ->
            java.sql.Timestamp.from(value)
        else -> value
    }

    companion object {
        val INSTANCE = TimeStampType()
    }
}


fun Table.registerTimestamp(name: String): Column<Instant> = registerColumn(name, TimeStampType())

