package pe.andy.bookholic.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookColorTest {

    private lateinit var bookColor: BookColor

    private var bgGreen: Int? = null
    private var bgGray: Int? = null

    @Before
    fun setUp() {
        val mContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookColor = BookColor(mContext)

        var field = BookColor::class.java.getDeclaredField("bgGreen")
        field.isAccessible = true
        bgGreen = field.getInt(bookColor)

        field = BookColor::class.java.getDeclaredField("bgGray")
        field.isAccessible = true
        bgGray = field.getInt(bookColor)
    }

    @Test
    fun testGetPlatformBGColor() {
        var actual = bookColor.getPlatformBGColor("교보문고")
        Assert.assertEquals(bgGreen, actual)

        actual = bookColor.getPlatformBGColor("")
        Assert.assertEquals(bgGray, actual)

    }
}