package tiddly.data

import javax.xml.bind.annotation.XmlType

/**
 * Date: 15/03/2018
 * Time: 00:51
 */
data class Status(
        val username: String = Status.DEFAULT_CREATOR,
        var space: HashMap<String, String> = HashMap<String, String>().apply {
            put("recipe", "all")
        }
) {
    companion object {
        const val DEFAULT_CREATOR = "GUEST"
    }
}

