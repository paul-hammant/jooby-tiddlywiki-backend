package tiddly

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import org.slf4j.LoggerFactory
import tiddly.data.Tiddler
import java.io.Closeable


/**
 * Date: 15/03/2018
 * Time: 02:41
 */
object DAO : Closeable {
    val log = LoggerFactory.getLogger(DAO::class.java)


    private lateinit var db: DB
    private lateinit var tiddlers: HTreeMap<String, Any>

    fun init() {
        db = DBMaker
                .fileDB("file.db")
                .fileMmapEnable()
                .make()

        tiddlers = db
                .hashMap("tiddlers", Serializer.STRING, Serializer.JAVA)
                .createOrOpen()
    }

    override fun close() {
        db.close()
    }

    // Probably tiddlers should be stored not in personal object but in HashMap.

    fun incrementTiddlerRev(tiddler: Tiddler) {
        val prevRevision = tiddlers.get(tiddler.title)
        if (prevRevision != null && prevRevision is Tiddler) {
            tiddler.revision = prevRevision.revision + 1
        }
    }

    fun saveTiddler(tiddler: Tiddler) {
        tiddlers.put(tiddler.title, tiddler)
    }

    fun deleteTiddler(tiddler: String) {
        tiddlers.remove(tiddler)
    }

    fun loadTiddler(name: String): Tiddler? {
        val tiddler = tiddlers[name]
        if (tiddler is Tiddler) {
            return tiddler
        } else {
            log.warn("Somehow you stored a value which is not Tiddler. Name '{}' value '{}'", name, tiddler)
        }

        return null
    }

    fun listTiddlers(): List<Tiddler> {
        val result = ArrayList<Tiddler>(tiddlers.values.size)
        tiddlers.values.forEach {
            if (it is Tiddler) {
                result.add(it)
            } else {
                log.warn("tiddlers collection contains {} instance", it?.javaClass?.name.toString())
            }
        }

        return result
    }
}
