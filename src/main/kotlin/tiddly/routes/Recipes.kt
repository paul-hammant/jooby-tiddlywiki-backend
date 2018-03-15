package tiddly.routes

import org.jooby.Result
import org.jooby.Results
import org.jooby.mvc.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tiddly.DAO
import tiddly.data.Tiddler
import tiddly.toHexString
import java.security.MessageDigest

/**
 * Date: 14/03/2018
 * Time: 23:05
 */
@Path("/recipes")
class Recipes {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Path("/")
    @Produces("application/json")
    @GET
    fun recipes(): List<String>{
        //TODO: this is stub
        return ArrayList<String>().apply {
            add("all")
        }
    }

    @Path("/:recipeName/tiddlers.json")
    @Produces("application/json")
    @GET
    fun getTiddlers(recipeName: String): List<Tiddler>{
        log.trace("Serving $recipeName")
        return DAO.listTiddlers()
    }

    @Path("/:recipeName/tiddlers/:tiddlerTitle")
    @Produces("application/json")
    @GET
    fun getTiddler(recipeName: String, tiddlerTitle: String): Tiddler?{
        log.trace("Serving $recipeName — $tiddlerTitle")
        return DAO.loadTiddler(tiddlerTitle)
    }


    @Path("/:recipeName/tiddlers/:tiddlerTitle")
    @Produces("application/json")
    @PUT
    fun putTiddler(recipeName: String, tiddlerTitle: String, @Body tiddler: Tiddler): Result{
        DAO.incrementTiddlerRev(tiddler)
        log.info("Putting tiddler {} with rev {}", tiddler.title, tiddler.revision)
        DAO.saveTiddler(tiddler)

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(tiddler.toString().toByteArray())
        val eTag: String
        eTag = String.format(
                "\"bag/%s/%d:%s\"",
                tiddler.title,
                tiddler.revision,
                digest.toHexString())


        return Results.with(200).header("Etag", eTag)
    }

    @Path("/:recipeName/tiddlers/\$:**/*")
    @Produces("application/json")
    @PUT
    fun putStoryList(@Body body: HashMap<String, Any>): Result {
        // TODO: Stub
        log.trace("Received:\n{}", body)

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(body.toString().toByteArray())
        val eTag: String
        eTag = String.format(
                "\"bag/%s/%d:%s\"",
                body["title"],
                1,
                digest.toHexString())


        return Results.with(200).header("Etag", eTag)
    }
}
