import tiddly.DAO
import tiddly.data.Tiddler


open class TestDAO : DAO {
    override fun init(dbFileName: String, testing: Boolean) {
    }

    override fun close() {
    }

    override fun incrementTiddlerRev(tiddler: Tiddler) {
    }

    override fun saveTiddler(tiddler: Tiddler) {
    }

    override fun deleteTiddler(tiddler: String): Boolean {
        return false
    }

    override fun loadTiddler(name: String): Tiddler? {
        return null
    }

    override fun listTiddlers(): List<Tiddler> {
        return emptyList()
    }

    override fun saveSetting(setting: HashMap<String, Any>) {
    }

    override fun loadSetting(name: String): HashMap<String, Any>? {
        return null
    }
}
