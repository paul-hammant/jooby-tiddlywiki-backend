package tiddly

import tiddly.data.Tiddler

/**
 * @author Klaus Schwartz aka tntclaus
 */
interface DAO {

    fun init(dbFileName: String, testing: Boolean = false)

    fun close()

    fun incrementTiddlerRev(tiddler: Tiddler)

    fun saveTiddler(tiddler: Tiddler)

    fun deleteTiddler(tiddler: String): Boolean

    fun loadTiddler(name: String): Tiddler?

    fun listTiddlers(): List<Tiddler>

    @Throws(IllegalArgumentException::class)
    fun saveSetting(setting: HashMap<String, Any>)

    fun loadSetting(name: String): HashMap<String, Any>?
}
