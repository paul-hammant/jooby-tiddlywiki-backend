import io.restassured.RestAssured
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jooby.Status
import tiddley.jooby
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
        on("/") {
            it("GET / should return html document") {
                RestAssured
                        .get("/")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .contentType("text/html")
            }

            it("OPTIONS should not return DAV header") {
                RestAssured
                        .options("/")
                        .header("dav")
                        .let {
                            assertNull(it)
                        }
            }
        }

        on("/auth") {
            it("GET should return \"Not implemented\"") {
                RestAssured
                        .get("/auth")
                        .body
                        .asString()
                        .let {
                            assertEquals("\"Not implemented\"", it)
                        }
            }
        }

        on("/status") {
            it("GET should return status object") {
                RestAssured
                        .get("/status")
                        .then()
                        .statusCode(200)
                        .body("username", equalTo("GUEST"))
                        .body("space.recipe", equalTo("all"))
            }
        }

        on("/bags") {
            mockDAO.setTestDAO(object : TestDAO(){
                override fun deleteTiddler(tiddler: String): Boolean {
                    return tiddler == "Existing Tiddler"
                }
            })

            it("DELETE existing Tiddler should return 200") {
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

            it("DELETE of non existing Tiddler should return 404") {
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

        on("/recipes") {
            it("GET /recipes should return [\"all\"]!") {
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



            it("GET list of tiddlers should have 2 elements") {
                mockDAO.setTestDAO(object : TestDAO(){
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
                        .body("creator", hasItems("GUEST", "GUEST"))
                        .body("type", hasItems("text/vnd.tiddlywiki"))
                        .body("title", hasItems("Tiddler1", "Tiddler2"))
            }

            it("GET existing Tiddler should have title, creator and type") {
                mockDAO.setTestDAO(object : TestDAO(){
                    override fun loadTiddler(name: String): Tiddler? {
                        if(name == "Tiddler1")
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
            }

            it("PUT new tiddler parses JSON correctly") {
                var storedTiddler: Tiddler? = null
                mockDAO.setTestDAO(object : TestDAO(){
                    override fun saveTiddler(tiddler: Tiddler) {
                        storedTiddler = tiddler
                    }
                })

                val tiddlerToSave = Tiddler("Tiddler1", "Tiddler1 text")
                RestAssured
                        .given()
                        .contentType("application/json")
                        .body(tiddlerToSave)
                        .`when`()
                        .put("/recipes/all/tiddlers/Tiddler1")
                        .then()
                        .assertThat()
                        .statusCode(Status.OK.value())

                assertEquals(tiddlerToSave, storedTiddler)
            }

            it("PUT \$:/StoryList should be parsed and stored correctly") {
                var storedSetting: HashMap<String, Any>? = null

                mockDAO.setTestDAO(object : TestDAO(){
                    override fun saveSetting(setting: HashMap<String, Any>) {
                        storedSetting = setting
                    }
                })

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
})
