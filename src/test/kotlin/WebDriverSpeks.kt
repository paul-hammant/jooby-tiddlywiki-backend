import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
//import org.seleniumhq.selenium.fluent.FluentWebDriver

class WebDriverSpeks : Spek({
    describe("tiddlywiki") {
        //val co = ChromeOptions()
        //val DRIVER = ChromeDriver(co)
        // val FWD = FluentWebDriver(DRIVER)

        it("should be nameable") {
            //FWD.div().title().shouldBe("something")
        }

    }
})
