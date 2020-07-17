package com.github.tonybaines

import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.none

object WebApp {
    fun providerFor(req: HttpRequest): ResponseProvider = {
        when (req.uri) {
            "/some/valid/path" -> Some("Hello World!")
            "/another/valid/path" -> Some("42")
            else -> none()
        }
    }

    fun handle(httpRequest: HttpRequest): HttpResponse =
        providerFor(httpRequest)()
            .map { body ->  HttpResponse(request = httpRequest, body = body)}
            .getOrElse { HttpResponse(httpRequest, 404, "") }

}
