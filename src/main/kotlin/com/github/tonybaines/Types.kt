package com.github.tonybaines


enum class Method { GET, PUT, POST, DELETE }

data class HttpRequest(val method: Method, val uri: String, val params: Map<String,Any> = emptyMap())

data class HttpResponse(val request: HttpRequest, val status: Int = 200, val body: String)

typealias ResponseProvider = (HttpRequest) -> Outcome

sealed class MaybeValid {
    class Valid(var provider: ResponseProvider) : MaybeValid()
    sealed class Invalid(val status: Int, var reason: String) : MaybeValid() {
        class NotFound() : Invalid(404,"Not Found")
        class BadRequest() : Invalid(400,"Bad Request")
    }
}

/**
 * Something went wrong
 */
sealed class Outcome {
    class Good(val status: Int, val body: String) : Outcome() {}

    sealed class Bad(var status: Int) : Outcome() {
        abstract val reason: String

        class JustBad(status: Int, val message: String) : Bad(status) {
            override val reason: String
                get() = message

        }

        class ExceptionallyBad(status: Int, val e: Throwable) : Bad(status) {
            override val reason: String
                get() = e?.localizedMessage?:"FAILED"
        }
    }
}