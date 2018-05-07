import tiddly.DAO
import tiddly.data.Tiddler

/**
 * Date: 07/05/2018
 * Time: 18:55
 */
class MockDAO(private var testDAO: TestDAO = TestDAO()): DAO {
    fun setTestDAO(dao: TestDAO) {
        testDAO = dao
    }


    override fun init(dbFileName: String, testing: Boolean) {
        testDAO.init(dbFileName, testing)
    }

    override fun close() {
        testDAO.close()
    }

    override fun incrementTiddlerRev(tiddler: Tiddler) {
        testDAO.incrementTiddlerRev(tiddler)
    }

    override fun saveTiddler(tiddler: Tiddler) {
        testDAO.saveTiddler(tiddler)
    }

    override fun deleteTiddler(tiddler: String): Boolean {
        return testDAO.deleteTiddler(tiddler)
    }

    override fun loadTiddler(name: String): Tiddler? {
        return testDAO.loadTiddler(name)
    }

    override fun listTiddlers(): List<Tiddler> {
        return testDAO.listTiddlers()
    }

    override fun saveSetting(setting: HashMap<String, Any>) {
        testDAO.saveSetting(setting)
    }

    override fun loadSetting(name: String): HashMap<String, Any>? {
        return testDAO.loadSetting(name)
    }

}
