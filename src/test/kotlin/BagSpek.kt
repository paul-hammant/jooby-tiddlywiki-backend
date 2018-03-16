import com.natpryce.hamkrest.Matcher
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import tiddly.data.Bag
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo

class SimpleTest : Spek({
    describe("a calculator") {
        val bag = Bag()

        it("should be nameable") {
            bag.name = "foo"
            assert.that(bag.name, equalTo("foo"))
        }

    }
})
