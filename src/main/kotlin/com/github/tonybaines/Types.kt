package com.github.tonybaines

import arrow.fx.IO

enum class Method {GET, PUT, POST, DELETE}

data class HttpRequest(val method: Method, val uri: String)

data class HttpResponse(val request: HttpRequest, val status: Int = 200, val body: String)

typealias ResponseProvider = () -> IO<Result>
typealias Result = Pair<Int, String>