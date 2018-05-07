package tiddly.data

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Date: 15/03/2018
 * Time: 00:27
 */
data class Tiddler(
        var title: String = "",
        var text: String = "",
        var creator: String = Status.DEFAULT_CREATOR
) : Serializable {
    //    The name of the tiddler. Required.
    //    A string representing when this tiddler was created.
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmssSSS")
    var created: Date = Date()

    /**
     * A string representing when this tiddler was last changed. Defaults to now.
     */
    @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmssSSS")
    var modified: Date = Date()

    /**
     * A string representing a personage that changed this tiddler in
     * some way. This doesn't necessarily have any assocation with the
     * tiddlyweb.usersign, though it may.
     */
    var modifier: String = ""

    //    A linest of strings that describe the tiddler.
    var tags: MutableList<String> = ArrayList()

    //    An arbitrary dictionary of extended (custom) fields on the tiddler.
    var fields: HashMap<String, String> = HashMap()

    //    The contents of the tiddler. A string.

    //    The revision of this tiddler. The type of a revision is unspecified
//    and is :py:class:`store <tiddlyweb.store.Store>` dependent.
    var revision: Long = 0L

    //    The name of the bag in which this tiddler exists, if any.
    var bag: String = "bag"
    //    The name of the recipe in which this tiddler exists, if any.
    var recipe: String = "all"

//    A reference to the :py:class:`Store <tiddlyweb.store.Store>` object
//    which retrieved this tiddler from persistent storage.
//    store

    var type: String? = null


    override fun toString(): String {
        return "Tiddler(title='$title', text='$text', creator='$creator', created=$created, modified=$modified, modifier='$modifier', tags=$tags, fields=$fields, revision=$revision, recipe=$recipe, type=$type)"
    }

    val uri: String? = null
//        get() = "http://localhost:8080/bags/$bag/tiddlers/$title"

//    val permissions = ArrayList<String>().apply {
//        add("read")
//    }
    val permissions: String? = null
}
