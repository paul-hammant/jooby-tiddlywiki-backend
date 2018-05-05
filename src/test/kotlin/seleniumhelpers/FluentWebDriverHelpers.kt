package seleniumhelpers

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.seleniumhq.selenium.fluent.FluentBy
import org.seleniumhq.selenium.fluent.FluentWebElement
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

inline fun WebElement.doWhenClickable(driver: WebDriver, timeout: Long, block: (element: WebElement) -> Unit) {
    val wait = WebDriverWait(driver, timeout)

    val clickableElement = wait.until(
            ExpectedConditions.elementToBeClickable(
                    wait.until(ExpectedConditions.visibilityOf(this))
            )
    )

    block(clickableElement)
}

inline fun FluentWebElement.doWhenClickable(
        driver: WebDriver,
        timeout: Long,
        block: (element: WebElement) -> Unit) {
    this.webElement.doWhenClickable(driver, timeout, block)
}

/**
 * Wait
 */
fun WebDriver.doWhenClickable(element: WebElement, timeout: Long, block: (element: WebElement) -> Unit) {
    val wait = WebDriverWait(this, timeout)

    val clickableElement = wait.until(
            ExpectedConditions.elementToBeClickable(
                    element
            )
    )

    block(clickableElement)
}
