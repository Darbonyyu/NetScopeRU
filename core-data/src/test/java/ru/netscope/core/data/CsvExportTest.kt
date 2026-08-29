package ru.netscope.core.data
import kotlin.test.Test
import kotlin.test.assertEquals
class CsvExportTest { @Test fun csvValuesWithCommasAreQuoted() { val value = "Теле2, регион"; val escaped = if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"${value.replace("\"", "\"\"")}\"" else value; assertEquals("\"Теле2, регион\"", escaped) } }
