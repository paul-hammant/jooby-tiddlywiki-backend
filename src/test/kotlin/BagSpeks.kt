import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import tiddly.data.Bag
import kotlin.test.assertEquals

class BagSpeks : Spek({
    describe("a bag") {
        val bag = Bag()

        it("should be nameable") {
            bag.name = "foo"
            assertEquals(bag.name, "foo")
        }
    }
})
