package com.github.tonybaines

object WebApp {
    fun handle(httpRequest: HttpRequest): HttpResponse = HttpResponse(httpRequest, "Hello World!")
}
