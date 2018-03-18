//import com.nhaarman.mockito_kotlin.*
import io.github.bonigarcia.wdm.FirefoxDriverManager
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.seleniumhq.selenium.fluent.FluentBy
import org.seleniumhq.selenium.fluent.FluentWebDriver
import tiddley.jooby
import tiddly.TiddlyApp
import tiddly.data.Tiddler
import kotlin.test.assertEquals

class WebDriverSpeks : Spek({

    val daoCalls = StringBuilder()
    val dao = object: TestDAO() {

        override fun init(dbFileName: String, testing: Boolean) {
            daoCalls.append(".init(" + dbFileName + ", " + testing+ ")")
        }

        override fun listTiddlers(): List<Tiddler> {
            daoCalls.append(".listTiddlers()")
            return ArrayList<Tiddler>()
        }

        override fun saveSetting(setting: HashMap<String, Any>) {
            daoCalls.append(".saveSetting(" + setting + ")")
        }

        override fun saveTiddler(tiddler: Tiddler) {
            daoCalls.append(".saveTiddler(" + tiddler + ")")
        }

        override fun deleteTiddler(tiddler: String) {
            daoCalls.append(".deleteTiddler(" + tiddler + ")")
        }

        override fun loadTiddler(name: String): Tiddler? {
            daoCalls.append(".loadTiddler(" + name + ")")
            return Tiddler("a", "b")
        }

        override fun loadSetting(name: String): HashMap<String, Any>? {
            daoCalls.append(".loadSetting(" + name + ")")
            return HashMap<String, Any>()
        }

        override fun close() {
            daoCalls.append(".close()")
        }

        override fun incrementTiddlerRev(tiddler: Tiddler) {
            daoCalls.append(".incrementTiddlerRev(" + tiddler + ")")
        }
    }

    val app = TiddlyApp(dao)

    jooby(app) {
        FirefoxDriverManager.getInstance().setup()
        val co = FirefoxOptions()
        val driver = FirefoxDriver(co)
//        val driver = ChromeDriver()
        val fwd = FluentWebDriver(driver)
        var testNum : Int = 1

        beforeGroup {
            assertEquals("", noInit(daoCalls.toString()))
        }

        beforeEachTest {
            daoCalls.setLength(0)
            driver.get("http://t" + testNum++ + ".devd.io:8080/")
        }

        describe("tiddlywiki") {

            it("should have title") {
                fwd.title().shouldBe("My TiddlyWiki — a non-linear personal web notebook")
                assertEquals(".listTiddlers()", noInit(daoCalls.toString()))
            }

            it("should be able to create a tiddler") {

                fwd.button(FluentBy.attribute("title","Create a new tiddler"))
                        .click()

                fwd.input(FluentBy.attribute("type","text"))
                     .click().clearField().sendKeys("My New tiddler title!")

                driver.switchTo().frame(driver.findElement(By.tagName("iframe")));

                fwd.textarea()
                        .clearField()
                        .sendKeys("My New tiddler!")

                driver.switchTo().defaultContent()

                fwd.button(FluentBy.attribute("title","Confirm changes to this tiddler"))
                        .click()

                Thread.sleep(200)

                assertEquals(".listTiddlers().saveSetting({text=, title=\$:/StoryList, fields={list=[[Draft of 'New Tiddler']]}, type=text/vnd.tiddlywiki})", noInit(daoCalls.toString()))

                // need to kill the "are you sure you want to close this page without saving" dialog

            }

//            it("some other spek") {
// //               fluentWebDriver.span()
//
//                // ERROR: Caught exception [ERROR: Unsupported command [selectFrame | relative=parent | ]]
//                driver.findElement(By.xpath("//span[3]/button")).click()
//                driver.findElement(By.xpath("//button[2]")).click()
//                driver.findElement(By.linkText("New Tiddler")).click()
//                driver.findElement(By.xpath("//button[3]")).click()
//                driver.findElement(By.xpath("//input[@type='checkbox']")).click()
//                driver.findElement(By.xpath("//p/button")).click()
//                driver.findElement(By.xpath("//span[13]/button")).click()
//                driver.findElement(By.xpath("//button[2]")).click()
//                driver.findElement(By.linkText("New Tiddler")).click()
//                driver.findElement(By.xpath("//span/span[7]/button")).click()
//                driver.findElement(By.xpath("//input[@type='text']")).click()
//                driver.findElement(By.xpath("//input[@type='text']")).clear()
//                fluentWebDriver.element(By.xpath("//input[@type='text']")).sendKeys("New Tiddler Edited")
//                fluentWebDriver.element(By.xpath("//span[3]/button")).click()
//                fluentWebDriver.element(By.xpath("//span/span[7]/button")).click()
//                fluentWebDriver.element(By.xpath("//section/div/div/div/div/span/span/button")).click()
//
//                assertTrue(driver.closeAlertAndGetItsText(true)
//                        .matches(Regex("^Do you wish to delete the tiddler \"New Tiddler Edited\"[\\s\\S]$")))

//            }
        }


        afterGroup {
            driver.close()
            driver.quit()
        }

    }

})

fun noInit(toString: String): String {
    return toString.replace(".init(test.db, true)","")
}




