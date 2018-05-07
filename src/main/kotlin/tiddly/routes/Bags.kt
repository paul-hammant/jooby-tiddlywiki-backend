package tiddly.routes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tiddly.DAO

/**
 * Date: 15/03/2018
 * Time: 02:12
 */
class Bags(private val dao: DAO) {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun deleteTiddler(tiddlerTitle: String): Boolean {
        log.debug("gonna delete {}", tiddlerTitle)
        return dao.deleteTiddler(tiddlerTitle)
    }
}
