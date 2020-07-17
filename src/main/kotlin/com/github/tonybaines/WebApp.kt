package com.github.tonybaines

object WebApp {
    fun foo(req: HttpRequest): ResponseProvider = {
        if (req.uri == "/some/valid/path") "Hello World!"
        else "42"
    }

    fun handle(httpRequest: HttpRequest): HttpResponse =
        HttpResponse(httpRequest, foo(httpRequest).invoke())
}
