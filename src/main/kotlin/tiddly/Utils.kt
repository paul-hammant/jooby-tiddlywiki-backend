package tiddly

import org.slf4j.LoggerFactory
import java.security.MessageDigest

fun ByteArray.toHexString(): String {
    val hexString = StringBuffer()

    this.forEach {
        val hex = Integer.toHexString(0xff and it.toInt())
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }

    return hexString.toString()
}

object TiddlyUtils {
    val log = LoggerFactory.getLogger(TiddlyUtils::class.java)

    fun eTag(title: String,
             bag: String,
             body: String,
             revision: Long): String {

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(body.toByteArray())
        val eTag: String
        eTag = String.format(
                "\"$bag/%s/%d:%s\"",
                title,
                revision,
                digest.toHexString())

        log.trace("Generated eTag '{}' for '{}' ", eTag, title)
        return eTag
    }
}
