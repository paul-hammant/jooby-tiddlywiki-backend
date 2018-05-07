package alpha

import com.fasterxml.jackson.annotation.JsonFormat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import tiddly.data.Tiddler
import kotlin.reflect.full.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TiddlerSpec : Spek({
    val tiddlerClassMembers = Tiddler().javaClass.kotlin.declaredMemberProperties
    val fieldNames = tiddlerClassMembers.map { it.name }


    describe("tiddler object structure") {

        it("should have title field") {
            assertTrue { fieldNames.contains("title") }
        }

        it("should have text field") {
            assertTrue { fieldNames.contains("text") }
        }

        it("should have creator field") {
            assertTrue { fieldNames.contains("creator") }
        }

        it("should have created field") {
            assertTrue { fieldNames.contains("created") }
        }

        it("should have modified field") {
            assertTrue { fieldNames.contains("modified") }
        }

        it("should have modifier field") {
            assertTrue { fieldNames.contains("modifier") }
        }

        it("should have tags field") {
            assertTrue { fieldNames.contains("tags") }
        }

        it("should have fields field") {
            assertTrue { fieldNames.contains("fields") }
        }

        it("should have revision field") {
            assertTrue { fieldNames.contains("revision") }
        }

        it("should have bag field") {
            assertTrue { fieldNames.contains("bag") }
        }

        it("should have recipe field") {
            assertTrue { fieldNames.contains("recipe") }
        }

        it("should have type field") {
            assertTrue { fieldNames.contains("type") }
        }

        it("should have uri field") {
            assertTrue { fieldNames.contains("uri") }
        }

        it("should have permissions field") {
            assertTrue { fieldNames.contains("permissions") }
        }
    }

    describe("tiddler object date fields JSON serialisation") {
        on("serialisation of created field ") {
            val property = tiddlerClassMembers.find { it.name == "created" }!!
            val jsonFormat = property.getter.findAnnotation<JsonFormat>()

            it("should have JsonFormat annotation") {
                assertNotNull(jsonFormat)
            }

            it("should have date format yyyyMMddHHmmssSSS") {
                jsonFormat as JsonFormat
                assertTrue { jsonFormat.shape == JsonFormat.Shape.STRING }
                assertTrue { jsonFormat.pattern == "yyyyMMddHHmmssSSS" }
            }
        }

        on("serialisation of modified field ") {
            val property = tiddlerClassMembers.find { it.name == "modified" }!!
            val jsonFormat = property.getter.findAnnotation<JsonFormat>()

            it("should have JsonFormat annotation") {
                assertTrue { jsonFormat is JsonFormat }
            }


            it("should have date format yyyyMMddHHmmssSSS") {
                jsonFormat as JsonFormat
                assertTrue { jsonFormat.shape == JsonFormat.Shape.STRING }
                assertTrue { jsonFormat.pattern == "yyyyMMddHHmmssSSS" }
            }
        }
    }
})
