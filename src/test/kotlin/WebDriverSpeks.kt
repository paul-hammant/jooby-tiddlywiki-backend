import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import io.github.bonigarcia.wdm.ChromeDriverManager
import org.jetbrains.spek.api.dsl.on
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.seleniumhq.selenium.fluent.FluentWebDriver
import seleniumhelpers.assertEquals


class WebDriverSpeks : Spek({
    ChromeDriverManager.getInstance().setup()
    val co = ChromeOptions()
    val chromeDriver = ChromeDriver(co)
    val fluentWebDriver = FluentWebDriver(chromeDriver)

    beforeGroup {
        chromeDriver.get("file://" + System.getProperty("user.dir") + "/public/index.html")
    }

    describe("tiddlywiki") {
        it("should have index.html") {
            fluentWebDriver.title().assertEquals("My TiddlyWiki — a non-linear personal web notebook")
        }
    }


    afterGroup {
        chromeDriver.close()
    }
})
