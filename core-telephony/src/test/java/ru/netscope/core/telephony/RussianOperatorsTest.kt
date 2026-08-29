package ru.netscope.core.telephony
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.netscope.core.telephony.model.RussianOperators
class RussianOperatorsTest { @Test fun resolvesRussianMts() { assertEquals("МТС", RussianOperators.find(250, 1)?.name) }; @Test fun rejectsNonRussianMcc() { assertNull(RussianOperators.find(255, 1)) } }
