package tiddly

import com.typesafe.config.Config
import org.jooby.Kooby
import org.jooby.Results
import org.jooby.json.Jackson
import org.jooby.run
import org.jooby.rx.Rx
import tiddly.data.Status
import tiddly.data.Tiddler
import tiddly.routes.Bags
import tiddly.routes.Recipes

/**
 * Date: 14/03/2018
 * Time: 21:32
 */
open class App constructor(val dao: DAO) : Kooby({

    val HashMap_String_Any = HashMap<String, Any>().javaClass

    assets("/", "index.html")
    use(Jackson())

    /**
     * TODO: Utilize Rx. We use netty which means DAO requests should be performed on separate
     * TODO: threads.
     */
    use(Rx())

    use(Recipes::class.java)
    use(Bags::class.java)

    val recipes = Recipes(dao)
    val bags = Bags(dao)

    onStart {
        val conf = require(Config::class.java)
        val dbFileName = conf.getString("mapdb.file")
        val dbTesting = if(conf.hasPath("mapdb.testing")) conf.getBoolean("mapdb.testing") else false
        dao.init(dbFileName, dbTesting)
    }

    onStop {
        dao.close()
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

    path("/bags") {
        delete ("/:bagName/tiddlers/:tiddlerTitle"){ req ->
            bags.deleteTiddler(req.param("tiddlerTitle").value())
        }
    }

    path("/recipes") {
        get("/") { req, resp ->
            resp.send(recipes.recipes())
        }
        get("/:recipeName/tiddlers.json") { req ->
            recipes.getTiddlers(req.param("recipeName").value())
        }
        get("/:recipeName/tiddlers/:tiddlerTitle") { req ->
            recipes.getTiddler(req.param("recipeName").value(),
                    req.param("tiddlerTitle").value())
        }
        put("/:recipeName/tiddlers/:tiddlerTitle") { req ->
            Results.with(200).header("Etag", recipes.putTiddler(req.param("recipeName").value(),
                    req.param("tiddlerTitle").value(),
                    req.body(Tiddler::class.java)))
        }
        put("/:recipeName/tiddlers/\${title:.*}") { req ->
            Results.with(200).header("Etag", recipes.putSetting(req.param("title").value(),
                    req.body(HashMap_String_Any)))
        }
        get ("/:recipeName/tiddlers/\${title:.*}") {req ->
            recipes.getSetting(req.param("title").value())
        }
    }
})

fun main(args: Array<String>) {
    run({ -> App(MapDbDAO()) }, *args)
}

