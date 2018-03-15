package tiddly

fun ByteArray.toHexString(): String {
    val hexString = StringBuffer()

    this.forEach {
        val hex = Integer.toHexString(0xff and it.toInt())
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }

    return hexString.toString()
}
