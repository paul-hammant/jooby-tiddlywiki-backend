package tiddly

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.jooby.Kooby
import org.jooby.json.Jackson
import org.jooby.run
import org.jooby.rx.Rx
import tiddly.data.Status
import tiddly.routes.Bags
import tiddly.routes.Recipes


/**
 * Date: 14/03/2018
 * Time: 21:32
 */
class App : Kooby({

    assets("/", "index.html")
    use(Jackson())

    /**
     * TODO: Utilize Rx. We use netty which means DAO requests should be performed on separate
     * TODO: threads.
     */
    use(Rx())

    use(Recipes::class.java)
    use(Bags::class.java)

    onStart {
        val conf = require(Config::class.java)
        val dbFileName = conf.getString("mapdb.file")
        val dbTesting = if(conf.hasPath("mapdb.testing")) conf.getBoolean("mapdb.testing") else false

        DAO.init(dbFileName, dbTesting)
    }

    onStop {
        DAO.close()
    }

    options("/") {
        //TiddlyWiki5 tries to know do we serve DAV requests via presence of "dav" header.
        //No header — no DAV support ^_^
    }

    get("/auth") {
        // TODO: Auth
        "Not implemented"
    }

    get("/status") {
        // TODO: Proper status
        Status()
    }
})

fun main(args: Array<String>) {
    run(::App, *args)
}

