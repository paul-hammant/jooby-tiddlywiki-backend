import tiddly.DAO
import tiddly.data.Tiddler

open class TestDAO : DAO {
    override fun init(dbFileName: String, testing: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun incrementTiddlerRev(tiddler: Tiddler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveTiddler(tiddler: Tiddler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTiddler(tiddler: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadTiddler(name: String): Tiddler? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listTiddlers(): List<Tiddler> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveSetting(setting: HashMap<String, Any>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadSetting(name: String): HashMap<String, Any>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}