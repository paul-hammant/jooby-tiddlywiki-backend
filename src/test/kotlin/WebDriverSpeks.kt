import io.github.bonigarcia.wdm.FirefoxDriverManager
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.seleniumhq.selenium.fluent.FluentWebDriver
import seleniumhelpers.assertEquals
import seleniumhelpers.closeAlertAndGetItsText
import tiddley.jooby
import tiddly.App
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue


class WebDriverSpeks : Spek({

    jooby(App()) {
        FirefoxDriverManager.getInstance().setup()
        val co = FirefoxOptions()
        val driver = FirefoxDriver(co)
        val fluentWebDriver = FluentWebDriver(driver)

        beforeGroup {
//            driver.get("file://" + System.getProperty("user.dir") + "/public/index.html")
            driver.get("http://localhost:8080/")
            println("beforeGroup")
        }

        describe("tiddlywiki") {
            it("should have title") {
                fluentWebDriver.title().assertEquals("My TiddlyWiki — a non-linear personal web notebook")
            }

            it("create tiddler") {
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                driver.findElement(By.xpath("//span[6]/button")).click()
                driver.findElement(By.xpath("//input[@type='text']")).click()
                // ERROR: Caught exception [ERROR: Unsupported command [selectFrame | index=0 | ]]
                driver.findElement(By.xpath("//textarea")).click()
                driver.findElement(By.xpath("//textarea")).clear()
                driver.findElement(By.xpath("//textarea")).sendKeys("My New tiddler!")
                // ERROR: Caught exception [ERROR: Unsupported command [selectFrame | relative=parent | ]]
                driver.findElement(By.xpath("//span[3]/button")).click()
                driver.findElement(By.xpath("//button[2]")).click()
                driver.findElement(By.linkText("New Tiddler")).click()
                driver.findElement(By.xpath("//button[3]")).click()
                driver.findElement(By.xpath("//input[@type='checkbox']")).click()
                driver.findElement(By.xpath("//p/button")).click()
                driver.findElement(By.xpath("//span[13]/button")).click()
                driver.findElement(By.xpath("//button[2]")).click()
                driver.findElement(By.linkText("New Tiddler")).click()
                driver.findElement(By.xpath("//span/span[7]/button")).click()
                driver.findElement(By.xpath("//input[@type='text']")).click()
                driver.findElement(By.xpath("//input[@type='text']")).clear()
                driver.findElement(By.xpath("//input[@type='text']")).sendKeys("New Tiddler Edited")
                driver.findElement(By.xpath("//span[3]/button")).click()
                driver.findElement(By.xpath("//span/span[7]/button")).click()
                driver.findElement(By.xpath("//section/div/div/div/div/span/span/button")).click()

                assertTrue(driver.closeAlertAndGetItsText(true)
                        .matches(Regex("^Do you wish to delete the tiddler \"New Tiddler Edited\"[\\s\\S]$")))

            }
        }


        afterGroup {
            driver.close()
            println("afterGroup")
        }

    }

})




