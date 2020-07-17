package com.github.tonybaines

enum class Method {GET, PUT, POST, DELETE}

data class HttpRequest(val method: Method, val uri: String)

data class HttpResponse(val request: HttpRequest, val body: String)
