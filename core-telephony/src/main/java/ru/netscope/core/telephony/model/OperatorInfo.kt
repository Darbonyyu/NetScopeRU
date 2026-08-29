package ru.netscope.core.telephony.model

data class OperatorInfo(val mnc: Int, val name: String, val isVirtual: Boolean)
object RussianOperators {
    private val known = listOf(OperatorInfo(1, "МТС", false), OperatorInfo(2, "МегаФон", false), OperatorInfo(99, "Билайн", false), OperatorInfo(20, "Теле2", false), OperatorInfo(4, "Сиблинк", false), OperatorInfo(7, "Скай Линк", false), OperatorInfo(8, "Смартс", false), OperatorInfo(10, "Ростелеком", false), OperatorInfo(11, "Yota", true), OperatorInfo(12, "АКОС", false), OperatorInfo(13, "Мотив", false), OperatorInfo(16, "Новая связь", false), OperatorInfo(17, "Ростелеком", false), OperatorInfo(18, "Тинькофф Мобайл", true), OperatorInfo(21, "ЭР-Телеком", false), OperatorInfo(23, "Данные", true), OperatorInfo(27, "Летай", false), OperatorInfo(28, "Вайнах Телеком", false), OperatorInfo(32, "СберМобайл", true), OperatorInfo(33, "Тинькофф Мобайл", true), OperatorInfo(39, "Ростелеком", false), OperatorInfo(40, "Газпромбанк Мобайл", true), OperatorInfo(49, "Крымтелеком", false), OperatorInfo(54, "Волна мобайл", false), OperatorInfo(55, "Миранда-медиа", false), OperatorInfo(56, "К-Телеком", false), OperatorInfo(57, "Крымтелеком", false), OperatorInfo(58, "Севтелеком", false), OperatorInfo(60, "Тинькофф Мобайл", true))
    fun find(mcc: Int?, mnc: Int?): OperatorInfo? = if (mcc == 250) known.firstOrNull { it.mnc == mnc } else null
}
