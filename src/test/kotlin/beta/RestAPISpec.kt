package beta

import MockDAO
import TestDAO
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jooby.Status
import jooby
import tiddly.TiddlyApp
import tiddly.data.Tiddler
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Date: 07/05/2018
 * Time: 15:54
 */

class RestAPISpec : Spek({
    val mockDAO = MockDAO()

    val app = TiddlyApp(mockDAO)

    jooby(app) {
        describe("jooby REST API") {

            beforeEachTest {
                mockDAO.setTestDAO(TestDAO())
            }

            on("GET /") {
                it("should return html document") {
                    RestAssured
                            .get("/")
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .contentType("text/html")
                }
            }

            on("OPTIONS /") {
                it("should not return DAV header") {
                    RestAssured
                            .options("/")
                            .header("dav")
                            .let {
                                assertNull(it)
                            }
                }
            }


            on("GET /auth") {
                it("should return \"Not implemented\"") {
                    RestAssured
                            .get("/auth")
                            .body
                            .asString()
                            .let {
                                assertEquals("\"Not implemented\"", it)
                            }
                }
            }

            on("GET /status") {
                it("should return status object") {
                    RestAssured
                            .get("/status")
                            .then()
                            .statusCode(200)
                            .body("username", equalTo("GUEST"))
                            .body("space.recipe", equalTo("all"))
                }
            }

            on("DELETE /bags") {
                mockDAO.setTestDAO(object : TestDAO() {
                    override fun deleteTiddler(tiddler: String): Boolean {
                        return tiddler == "Existing Tiddler"
                    }
                })

                it("should return 200 for existing Tiddler") {
                    RestAssured
                            .delete("/bags/bag/tiddlers/Existing%20Tiddler")
                            .then()
                            .statusCode(Status.OK.value())
                            .extract()
                            .asString()
                            .let {
                                assertEquals("", it)
                            }
                }

                it("should return 404 for non existing Tiddler") {
                    RestAssured
                            .delete("/bags/bag/tiddlers/NonExisting%20Tiddler")
                            .then()
                            .statusCode(Status.NOT_FOUND.value())
                            .extract()
                            .asString()
                            .let {
                                assertEquals("", it)
                            }
                }
            }

            on("GET /recipes") {
                it("should return [\"all\"]!") {
                    RestAssured.given()
                            .`when`()
                            .get("/recipes")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())
                            .extract()
                            .asString()
                            .let {
                                assertEquals(it, "[\"all\"]")
                            }
                }
            }

            on("GET /recipes/:recipe/tiddlers.json") {

                it("should return a list of tiddlers in a recipe") {
                    mockDAO.setTestDAO(object : TestDAO() {
                        override fun listTiddlers(): List<Tiddler> {
                            return ArrayList<Tiddler>().apply {
                                add(Tiddler("Tiddler1").apply { type = "text/vnd.tiddlywiki" })
                                add(Tiddler("Tiddler2").apply { type = "text/vnd.tiddlywiki" })
                            }
                        }
                    })

                    RestAssured.given()
                            .`when`()
                            .get("/recipes/all/tiddlers.json")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())
                            .body("creator", hasItems("GUEST"))
                            .body("type", hasItems("text/vnd.tiddlywiki"))
                            .body("title", hasItems("Tiddler1", "Tiddler2"))
                }
            }

            on("GET /recipes/:recipe/tiddlers/:tiddler") {

                it("should have title, text, creator and type") {
                    mockDAO.setTestDAO(object : TestDAO() {
                        override fun loadTiddler(name: String): Tiddler? {
                            if (name == "Tiddler1")
                                return Tiddler("Tiddler1").apply { type = "text/vnd.tiddlywiki" }

                            return null
                        }
                    })

                    RestAssured
                            .get("/recipes/all/tiddlers/Tiddler1")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())
                            .body("creator", equalTo("GUEST"))
                            .body("type", equalTo("text/vnd.tiddlywiki"))
                            .body("title", equalTo("Tiddler1"))
                            .body("text", equalTo(""))
                }
            }

            on("PUT /recipes/:recipe/tiddlers/:tiddler") {
                var storedTiddler: Tiddler? = null
                mockDAO.setTestDAO(object : TestDAO() {
                    override fun saveTiddler(tiddler: Tiddler) {
                        storedTiddler = tiddler
                    }
                })
                val tiddlerToSave = Tiddler("Tiddler1", "Tiddler1 text")

                it("should parse JSON correctly") {
                    RestAssured
                            .given()
                            .contentType("application/json")
                            .body(tiddlerToSave)
                            .`when`()
                            .put("/recipes/all/tiddlers/Tiddler1")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())

                }

                it("should save Tiddler to DAO") {
                    assertEquals(tiddlerToSave, storedTiddler)
                }

            }

            on("PUT \$:/StoryList setting") {
                var storedSetting: HashMap<String, Any>? = null

                mockDAO.setTestDAO(object : TestDAO() {
                    override fun saveSetting(setting: HashMap<String, Any>) {
                        storedSetting = setting
                    }
                })

                it("it should parse JSON correctly") {
                    RestAssured.given()
                            .contentType("application/json")
                            .body("{\n" +
                                    "    \"title\": \"\$:/StoryList\",\n" +
                                    "    \"text\": \"\",\n" +
                                    "    \"fields\": {\n" +
                                    "        \"list\": \"\"\n" +
                                    "    },\n" +
                                    "    \"type\": \"text/vnd.tiddlywiki\"\n" +
                                    "}")
                            .`when`()
                            .put("/recipes/all/tiddlers/%24%3A%2FStoryList")
                            .then()
                            .assertThat()
                            .statusCode(Status.OK.value())
                }

                it("should save \$:/StoryList setting to DAO") {
                    assertEquals("\$:/StoryList", storedSetting?.get("title"))
                }

//            it("GET /recipes/:recipeName/tiddlers/\${title:.*} ") {
//                RestAssured.given()
//                        .`when`()
//                        .get("/recipes")
//                        .then()
//                        .assertThat()
//                        .statusCode(Status.OK.value())
//                //TODO Frontend doesn't request this URI so far. Do we really need to test it?
//            }
            }
        }
    }
})
