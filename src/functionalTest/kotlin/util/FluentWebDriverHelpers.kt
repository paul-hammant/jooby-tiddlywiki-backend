package util

import org.openqa.selenium.ElementClickInterceptedException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.seleniumhq.selenium.fluent.FluentWebElement


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

    val visibleElement = wait.until(ExpectedConditions.visibilityOf(this))

    val clickableElement = wait.until(ExpectedConditions.elementToBeClickable(visibleElement))


    // Workaround for:
    // org.openqa.selenium.ElementClickInterceptedException: Element <button class="tc-btn-invisible">
    // is not clickable at point (592.7499847412109,90.74166870117188) because another element
    // <div class="tc-tiddler-frame tc-tiddler-view-frame tc-tiddler-exists   "> obscures it
    val interval = 10L
    var time = 0L
    while (true) {
        try {
            block(clickableElement)
            break
        } catch (e: ElementClickInterceptedException) {
            Thread.sleep(interval)
            time += interval
            if(time > timeout * 1000)
                throw e
        }
    }
}

inline fun FluentWebElement.doWhenClickable(
        driver: WebDriver,
        timeout: Long,
        block: (element: WebElement) -> Unit) {
    this.webElement.doWhenClickable(driver, timeout, block)
}
