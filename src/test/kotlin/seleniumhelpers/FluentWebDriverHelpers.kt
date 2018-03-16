package seleniumhelpers

import org.seleniumhq.selenium.fluent.TestableString
import kotlin.test.assertEquals

/**
 * Date: 16/03/2018
 * Time: 21:43
 */

fun TestableString.assertEquals(expected: String, message: String? = null) {
    assertEquals(expected, this.toString(), message)
}
