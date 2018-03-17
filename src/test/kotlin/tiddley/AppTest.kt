package tiddley

import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jooby.Status
import tiddly.App
import tiddly.DAO

/**
 * Date: 15/03/2018
 * Time: 17:28
 */
object AppTest : Spek({
    jooby(App(DAO())) {
        describe("Paths /recipes") {
            given("queryParameter name=Victor") {
                it("should return Hello Victor!") {
                    val name = "Victor"
                    given()
                            .queryParam("name", name)
                            .`when`()
                            .get("/recipes")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())
                            .extract()
                            .asString()
                            .let {
                                it shouldEqual "[\"all\"]"
                            }
                }
            }
        }
    }
})
