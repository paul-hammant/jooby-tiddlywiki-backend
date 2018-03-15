package tiddly.routes

import org.jooby.mvc.DELETE
import org.jooby.mvc.Path
import org.jooby.mvc.Produces
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tiddly.DAO

/**
 * Date: 15/03/2018
 * Time: 02:12
 */
@Path("/bags")
class Bags {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Path("/:bagName/tiddlers/:tiddlerTitle")
    @Produces("application/json")
    @DELETE
    fun delTiddler(tiddlerTitle: String) {
        log.debug("gonna delete {}", tiddlerTitle)
        DAO.deleteTiddler(tiddlerTitle)
    }
}
