package tiddley

import org.jetbrains.spek.api.dsl.SpecBody
import org.jooby.Jooby

/**
 * Date: 15/03/2018
 * Time: 17:32
 */
fun SpecBody.jooby(app: Jooby, body: SpecBody.() -> Unit) {
    beforeGroup {
        app.start(
                "server.join=false",
                "mapdb.file=test.db",
                "mapdb.testing=true"
        )
    }

    body()

    afterGroup {
        app.stop()

    }
}
