package pe.andy.bookholic.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StrTest {

    @Test
    fun testDef() {
        assertEquals("", Str.def(null))
        assertEquals("", Str.def(""))
        assertEquals("Hello", "He" + "llo")
    }

    @Test
    fun testExtractInt() {
        assertEquals(0, Str.extractInt())
        assertEquals(0, Str.extractInt(""))
        assertEquals(123, Str.extractInt("0123"))
        assertEquals(12345, Str.extractInt("12Test34테스트5"))
    }
}