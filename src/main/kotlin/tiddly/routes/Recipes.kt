package tiddly.routes

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

    fun recipes(): List<String> {
        //TODO: this is stub
        return ArrayList<String>().apply {
            add("all")
        }
    }

    fun getTiddlers(recipeName: String): List<Tiddler> {
        log.trace("getTiddlers($recipeName)")
        return dao.listTiddlers()
    }

    fun getTiddler(recipeName: String, tiddlerTitle: String): Tiddler? {
        val loadTiddler = dao.loadTiddler(tiddlerTitle)
        log.trace("getTiddler($recipeName, $tiddlerTitle, {})", loadTiddler.toString())
        return loadTiddler
    }


    fun putTiddler(recipeName: String, tiddlerTitle: String, tiddler: Tiddler): String {
        dao.incrementTiddlerRev(tiddler)
        log.info("putTiddler({}, {}, {})",
                recipeName,
                tiddlerTitle,
                tiddler.toString())
        dao.saveTiddler(tiddler)

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(tiddler.toString().toByteArray())
        return String.format(
                "\"bag/%s/%d:%s\"",
                tiddler.title,
                tiddler.revision,
                digest.toHexString())
    }

    fun putSetting(title: String, body: HashMap<String, Any>): String {
        log.trace("putSetting({}, {})", title, body)

        dao.saveSetting(body)

        return TiddlyUtils.eTag(
                body["title"] as String,
                "bag",
                body.toString(),
                if(body["revision"] == null) 0L else body["revision"] as Long
        )
    }

    fun getSetting(title: String): HashMap<String, Any>? {
        val setting = dao.loadSetting("\$$title")
        log.trace("getSetting({}, {})", title, setting)
        return setting
    }
}
