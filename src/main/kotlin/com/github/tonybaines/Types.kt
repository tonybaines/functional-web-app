package com.github.tonybaines

import java.util.function.Function
import java.util.function.Supplier

enum class Method {GET, PUT, POST, DELETE}

data class HttpRequest(val method: Method, val uri: String)

data class HttpResponse(val request: HttpRequest, val body: String)

typealias ResponseProvider = () -> String