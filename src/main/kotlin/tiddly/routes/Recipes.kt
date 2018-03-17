package tiddly.routes

import org.jooby.Result
import org.jooby.Results
import org.jooby.mvc.Body
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
class Recipes constructor(val dao: DAO) {

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun recipes(): List<String>{
        //TODO: this is stub
        return ArrayList<String>().apply {
            add("all")
        }
    }

    fun getTiddlers(recipeName: String): List<Tiddler>{
        log.trace("Serving $recipeName")
        return dao.listTiddlers()
    }

    fun getTiddler(recipeName: String, tiddlerTitle: String): Tiddler?{
        log.trace("Serving $recipeName — $tiddlerTitle")
        return dao.loadTiddler(tiddlerTitle)
    }


    fun putTiddler(recipeName: String, tiddlerTitle: String, @Body tiddler: Tiddler): Result{
        dao.incrementTiddlerRev(tiddler)
        log.info("Putting in recipe '{}' tiddler '{}' path title '{}' with revision {}",
                recipeName,
                tiddler.title,
                tiddlerTitle,
                tiddler.revision)
        dao.saveTiddler(tiddler)

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

    fun putSetting(title: String, @Body body: HashMap<String, Any>): Result {
        log.trace("Put title '{}':\n{}", title, body)

        dao.saveSetting(body)

        val eTag: String = TiddlyUtils.eTag(
                body["title"] as String,
                "bag",
                body.toString(),
                if(body["revision"] == null) 0L else body["revision"] as Long
        )

        return Results.with(200).header("Etag", eTag)
    }

    fun getSetting(title: String): HashMap<String, Any>? {
        val setting = dao.loadSetting("\$$title")
        log.trace("Get title '{}':\n{}", title, setting)

        return setting
    }
}
