package tiddly.routes

import org.jooby.Result
import org.jooby.Results
import org.jooby.mvc.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tiddly.DAO
import tiddly.TiddlyUtils
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
        log.info("Putting in recipe '{}' tiddler '{}' path title '{}' with revision {}",
                recipeName,
                tiddler.title,
                tiddlerTitle,
                tiddler.revision)
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

    @Path("/:recipeName/tiddlers/\${title:.*}")
    @Produces("application/json")
    @PUT
    fun putSetting(title: String, @Body body: HashMap<String, Any>): Result {
        log.trace("Put title '{}':\n{}", title, body)

        DAO.saveSetting(body)

        val eTag: String = TiddlyUtils.eTag(
                body["title"] as String,
                "bag",
                body.toString(),
                if(body["revision"] == null) 0L else body["revision"] as Long
        )

        return Results.with(200).header("Etag", eTag)
    }

    @Path("/:recipeName/tiddlers/\${title:.*}")
    @Produces("application/json")
    @GET
    fun getSetting(title: String): HashMap<String, Any>? {
        val setting = DAO.loadSetting("\$$title")
        log.trace("Get title '{}':\n{}", title, setting)

        return setting
    }
}
