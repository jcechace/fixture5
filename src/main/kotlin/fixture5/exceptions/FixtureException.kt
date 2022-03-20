package fixture5.exceptions

class FixtureException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
    constructor(cause: Exception) : super(cause)
}