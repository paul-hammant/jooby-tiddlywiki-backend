package integration

import tiddly.DummyDAO
import io.github.bonigarcia.wdm.FirefoxDriverManager
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.seleniumhq.selenium.fluent.FluentBy
import org.seleniumhq.selenium.fluent.FluentWebDriver
import tiddly.TiddlyApp
import tiddly.data.Tiddler
import util.closeAlertAndGetItsText
import util.doWhenClickable
import util.jooby
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class WebDriverSpec : Spek({

    val daoCalls = StringBuilder()
    val dao = object : DummyDAO() {

        override fun init(dbFileName: String, testing: Boolean) {
            daoCalls.append(".init($dbFileName, $testing)")
        }

        override fun listTiddlers(): List<Tiddler> {
            daoCalls.append(".listTiddlers()")
            return ArrayList()
        }

        override fun saveSetting(setting: HashMap<String, Any>) {
            daoCalls.append(".saveSetting($setting)")
        }

        override fun saveTiddler(tiddler: Tiddler) {
            daoCalls.append(".saveTiddler(${tiddler.title}, ${tiddler.text})")
        }

        override fun deleteTiddler(tiddler: String): Boolean {
            daoCalls.append(".deleteTiddler($tiddler)")
            return true
        }

        override fun loadTiddler(name: String): Tiddler? {
            daoCalls.append(".loadTiddler($name)")
            return Tiddler("a", "b")
        }

        override fun loadSetting(name: String): HashMap<String, Any>? {
            daoCalls.append(".loadSetting($name)")
            return HashMap()
        }

        override fun close() {
            daoCalls.append(".close()")
        }

        override fun incrementTiddlerRev(tiddler: Tiddler) {
            daoCalls.append(".incrementTiddlerRev(${tiddler.title})")
        }
    }

    val app = TiddlyApp(dao)

    jooby(app) {
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null")
        val co = FirefoxOptions()
        lateinit var driver: FirefoxDriver
        lateinit var fwd: FluentWebDriver
        var testNum = 1

        beforeGroup {
            FirefoxDriverManager.getInstance().setup()
//            driver = ChromeDriver()
            driver = FirefoxDriver(co)
            fwd = FluentWebDriver(driver)

            assertEquals("", daoCalls.toString().noInit())
            driver.get("http://t" + testNum++ + ".devd.io:8080/")
        }

        beforeEachTest {
            daoCalls.setLength(0)
        }

        describe("tiddlywiki") {

            it("should have title") {
                fwd.title().shouldBe("My TiddlyWiki — a non-linear personal web notebook")

                assertContains(
                        ".listTiddlers()",
                        daoCalls,
                        1500
                )
            }

            it("should be able to create a tiddler") {

                fwd.button(FluentBy.attribute("title", "Create a new tiddler"))
                        .click()

                fwd.input(FluentBy.attribute("type", "text"))
                        .click().clearField().sendKeys("My New tiddler title!")

                driver.switchTo().frame(driver.findElement(By.tagName("iframe")))

                fwd.textarea()
                        .clearField()
                        .sendKeys("My New tiddler!")

                driver.switchTo().defaultContent()

                fwd.button(FluentBy.attribute("title", "Confirm changes to this tiddler"))
                        .click()

                fwd
                        .button(FluentBy.attribute("title", "Close this tiddler"))
                        .doWhenClickable(driver, 2) {
                            it.click()
                        }


                assertContains(
                        ".saveTiddler(My New tiddler title!, My New tiddler!)",
                        daoCalls,
                        1500
                )

            }


            it("should be able to edit tiddler") {


                fwd.button(By.xpath("contains(.,'Recent')"))
                        .click()


                fwd.link(FluentBy.linkText("My New tiddler title!"))
                        .click()

                fwd.button(FluentBy.attribute("title", "Edit this tiddler"))
                        .click()


                fwd.input(FluentBy.attribute("type", "text"))
                        .click().clearField().sendKeys("Edited tiddler title")

                driver.switchTo().frame(driver.findElement(By.tagName("iframe")))

                fwd.textarea()
                        .clearField()
                        .sendKeys("Edited tiddler content")


                driver.switchTo().defaultContent()

                fwd.button(FluentBy.attribute("title", "Confirm changes to this tiddler"))
                        .click()

                assertContains(
                        ".deleteTiddler(My New tiddler title!).saveSetting({text=, title=\$:/StoryList, fields={list=[[Edited tiddler title]]}, type=text/vnd.tiddlywiki}).incrementTiddlerRev(Edited tiddler title).saveTiddler(Edited tiddler title, Edited tiddler content)",
                        daoCalls,
                        2000
                )
            }


            it("should be able to delete tiddler") {


                fwd.button(By.xpath("contains(.,'Recent')"))
                        .click()


                fwd.link(FluentBy.linkText("Edited tiddler title"))
                        .click()

                fwd.button(FluentBy.attribute("title", "Edit this tiddler"))
                        .doWhenClickable(driver, 1) {
                            it.click()
                        }

                fwd.button(FluentBy.attribute("title", "Delete this tiddler"))
                        .doWhenClickable(driver, 1) {
                            it.click()
                        }

                driver
                        .closeAlertAndGetItsText(true)
//                        .equals("Do you wish to delete the tiddler \"Edited tiddler title\"?")


                assertContains(
                        ".deleteTiddler(Edited tiddler title)",
                        daoCalls,
                        1500
                )
            }
        }


        afterGroup {
            // kill the "are you sure you want to close this page without saving" dialog
            driver.executeScript("window.onbeforeunload = null;")
            driver.quit()
        }

    }


})


inline fun waitFor(
        timeout: Long,
        interval: Long = 10,
        block: () -> Boolean) {
    var time = 0L
    while (time < timeout) {
        if (block()) break

        Thread.sleep(interval)
        time += interval
    }
    println("Time passed: ${time}ms")
}

fun String.noInit(): String {
    return this.replace(".init(test.db, true)", "")
}

fun assertContains(expected: CharSequence, actual: CharSequence) {
    assertTrue("Expected to find <$expected>, in actual <$actual>") {
        actual.contains(expected)
    }
}


fun assertContains(expected: CharSequence, actual: CharSequence, timeout: Long) {
    waitFor(timeout) {
        actual.contains(expected)
    }

    assertContains(expected, actual)
}

