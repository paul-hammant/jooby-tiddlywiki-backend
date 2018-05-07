package tiddly

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tiddly.data.Tiddler
import java.io.Closeable


/**
 * Date: 15/03/2018
 * Time: 02:41
 */
class MapDbDAO : Closeable, DAO {
    val log: Logger = LoggerFactory.getLogger(MapDbDAO::class.java)

    private lateinit var db: DB
    private lateinit var tiddlers: HTreeMap<String, Any>
    private lateinit var settings: HTreeMap<String, Any>

    override fun init(dbFileName: String, testing: Boolean) {
        log.info("Using mapdb '{}' file ", dbFileName)
        if (testing) {
            db = DBMaker
                    .fileDB(dbFileName)
                    .fileDeleteAfterOpen()
                    .fileDeleteAfterClose()
                    .fileMmapEnable()
                    .make()
            log.info("Created mapdb {} file for testing", dbFileName)
        } else {
            db = DBMaker
                    .fileDB(dbFileName)
                    .fileMmapEnable()
                    .make()
        }

        tiddlers = db
                .hashMap("tiddlers", Serializer.STRING, Serializer.JAVA)
                .createOrOpen()

        settings = db
                .hashMap("settings", Serializer.STRING, Serializer.JAVA)
                .createOrOpen()
    }

    override fun close() {
        db.close()
    }

    // Probably tiddlers should be stored not in personal object but in HashMap.

    override fun incrementTiddlerRev(tiddler: Tiddler) {
        val prevRevision = tiddlers[tiddler.title]
        if (prevRevision != null && prevRevision is Tiddler) {
            tiddler.revision = prevRevision.revision + 1
        }
    }

    override fun saveTiddler(tiddler: Tiddler) {
        tiddlers[tiddler.title] = tiddler
    }

    override fun deleteTiddler(tiddler: String): Boolean {
        tiddlers.remove(tiddler) ?: return false

        return true
    }

    override fun loadTiddler(name: String): Tiddler? {
        val tiddler = tiddlers[name]
        if (tiddler is Tiddler) {
            return tiddler
        } else {
            log.warn("Somehow you stored a value which is not Tiddler. Name '{}' value '{}'", name, tiddler)
        }

        return null
    }

    override fun listTiddlers(): List<Tiddler> {
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

    @Throws(IllegalArgumentException::class)
    override fun saveSetting(setting: HashMap<String, Any>) {
        val title = setting["title"]
        if (title is String) {
            settings[title] = setting
        } else {
            throw IllegalArgumentException("Setting has no 'title' field $setting")
        }
    }

    override fun loadSetting(name: String): HashMap<String, Any>? {
        val setting = settings[name]
        if (setting is HashMap<*, *> && setting.keys.first() is String) {
            @Suppress("UNCHECKED_CAST")
            return setting as HashMap<String, Any>
        }
        log.warn("Surprise! Your settings collection seems to be broken. Found setting which is not HashMap<String, " +
                "Any>: {} ", setting)

        return null
    }
}
