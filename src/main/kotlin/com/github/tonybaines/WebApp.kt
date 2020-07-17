package com.github.tonybaines

import arrow.core.*
import arrow.core.extensions.either.foldable.get
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Promise
import arrow.fx.fix
import java.lang.RuntimeException
import java.security.Provider

object WebApp {

    private val someValidPath: ResponseProvider = { IO.just(Result(200, "Hello World!")) }
    private val anotherValidPath: ResponseProvider = { IO.just(Result(200, "42")) }
    private val brokenPath: ResponseProvider = { IO.raiseError(RuntimeException("Whoops!")) }

    fun providerFor(req: HttpRequest): ResponseProvider = {
        when (req.uri) {
            "/some/valid/path" -> someValidPath()
            "/another/valid/path" -> anotherValidPath()
            "/broken/path" -> brokenPath()
            else -> IO.just(Result(404, ""))
        }
    }

    fun handle(httpRequest: HttpRequest): HttpResponse =
        providerFor(httpRequest)()
            .redeem(
                { t ->  HttpResponse(httpRequest, 500, t.localizedMessage)},
                { result -> HttpResponse(httpRequest, result.first, result.second) }
            ).unsafeRunSync()

}
