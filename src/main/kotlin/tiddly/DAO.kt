package tiddly

import tiddly.data.Tiddler

interface DAO {

    open fun init(dbFileName: String, testing: Boolean = false)

    open fun close()

    open fun incrementTiddlerRev(tiddler: Tiddler)

    open fun saveTiddler(tiddler: Tiddler)

    open fun deleteTiddler(tiddler: String)

    open fun loadTiddler(name: String): Tiddler?

    open fun listTiddlers(): List<Tiddler>

    @Throws(IllegalArgumentException::class)
    open fun saveSetting(setting: HashMap<String, Any>)

    open fun loadSetting(name: String): HashMap<String, Any>?
}