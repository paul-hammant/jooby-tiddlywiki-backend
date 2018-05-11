package unit

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import tiddly.data.Bag
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.assertTrue

class BagSpec : Spek({
    describe("bag object structure") {
        val fieldNames = Bag().javaClass.kotlin.declaredMemberProperties.map { it.name }

        it("should have name field") {
            assertTrue { fieldNames.contains("name") }
        }

        it("should have desc field") {
            assertTrue { fieldNames.contains("desc") }
        }

        it("should have policy field") {
            assertTrue { fieldNames.contains("policy") }
        }
    }
})
