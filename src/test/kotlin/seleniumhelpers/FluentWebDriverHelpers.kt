package seleniumhelpers

import org.openqa.selenium.WebDriver
import org.seleniumhq.selenium.fluent.TestableString
import kotlin.test.assertEquals

/**
 * Date: 16/03/2018
 * Time: 21:43
 */

fun TestableString.assertEquals(expected: String, message: String? = null) {
    assertEquals(expected, this.toString(), message)
}

fun WebDriver.closeAlertAndGetItsText(
        acceptNextAlert: Boolean): String {
    try {
        val alert = this.switchTo().alert()
        val alertText = alert.text
        if (acceptNextAlert) {
            alert.accept()
        } else {
            alert.dismiss()
        }

        this.switchTo().defaultContent()

        return alertText
    } finally {
    }
}
