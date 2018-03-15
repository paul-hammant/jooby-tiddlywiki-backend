package tiddly

/**
 * Date: 14/03/2018
 * Time: 23:46
 */
open class TiddlyException : Throwable {
    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause)
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}

open class PermissionsError(message: String) : TiddlyException(message)
class ForbiddenError(message: String) : PermissionsError(message)
class UserRequiredError(message: String) : PermissionsError(message)
