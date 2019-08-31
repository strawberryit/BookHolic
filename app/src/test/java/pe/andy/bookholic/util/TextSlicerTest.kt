package pe.andy.bookholic.util

import kotlin.test.Test
import kotlin.test.assertEquals

class TextSlicerTest {

    @Test
    fun `get() 메서드 동작 검증`() {
        val slicer = TextSlicer(" Hello | world ", "\\|")

        assertEquals("Hello", slicer.get(0))
        assertEquals("world", slicer.get(1))
        assertEquals("", slicer.get(2))
        assertEquals("", slicer.get(-1))
    }

    @Test
    fun `pop() 메서드 동작 검증`() {
        val slicer = TextSlicer(" Test :Hello | world ", " |:|\\|")

        assertEquals("Test", slicer.pop())
        assertEquals("", slicer.pop())
        assertEquals("Hello", slicer.pop())
        assertEquals("world", slicer.pop())
        assertEquals("", slicer.pop())
        assertEquals("", slicer.pop())
    }

}
