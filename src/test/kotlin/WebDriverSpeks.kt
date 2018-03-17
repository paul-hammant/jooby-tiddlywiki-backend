import io.github.bonigarcia.wdm.FirefoxDriverManager
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.seleniumhq.selenium.fluent.FluentBy
import org.seleniumhq.selenium.fluent.FluentWebDriver
import tiddley.jooby
import tiddly.App
import tiddly.DAO

class WebDriverSpeks : Spek({

    jooby(App(DAO())) {
        FirefoxDriverManager.getInstance().setup()
        val co = FirefoxOptions()
        val driver = FirefoxDriver(co)
//    val driver = ChromeDriver()
    val fwd = FluentWebDriver(driver)

        beforeGroup {
            driver.get("http://localhost:8080/")
        }

        describe("tiddlywiki") {

            it("should have title") {
                fwd.title().shouldBe("My TiddlyWiki — a non-linear personal web notebook")
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

                // need to kill the "are you sure you want to close this page without saving" dialog

            }

            it("some other spek") {
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

            }
        }


        afterGroup {
            driver.close()
            driver.quit()
        }

    }

})




