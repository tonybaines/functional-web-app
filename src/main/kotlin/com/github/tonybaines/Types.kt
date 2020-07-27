package com.github.tonybaines


enum class Method { GET, PUT, POST, DELETE }

data class HttpRequest(val method: Method, val uri: String)

data class HttpResponse(val request: HttpRequest, val status: Int = 200, val body: String)

typealias ResponseProvider = (HttpRequest) -> HttpResponse

sealed class MaybeValid {
    class Valid(var request: HttpRequest) : MaybeValid()
    sealed class Invalid(val status: Int, var reason: String) : MaybeValid() {
        class NotFound() : Invalid(404,"Not Found")
        class BadRequest() : Invalid(400,"Bad Request")
    }
}

/**
 * Something went wrong
 */
sealed class Outcome {
    sealed class Good(val status: Int, val body: String) : Outcome() {}

    sealed class Bad : Outcome() {
        abstract val reason: String

        class JustBad(var status: Int, val message: String) : Bad() {
            override val reason: String
                get() = message

        }

        class ExceptionallyBad(var status: Int, val e: Exception) : Bad() {
            override val reason: String
                get() = e.localizedMessage
        }
    }
}