package com.github.tonybaines

import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds


object WebApp {

    private val someValidPath: ResponseProvider = IO.just(Result(200, "Hello World!"))
    private val anotherValidPath: ResponseProvider = IO.just(Result(200, "42"))
    private val brokenPath: ResponseProvider = IO.raiseError(RuntimeException("Whoops!"))
    private val notFound: ResponseProvider = IO.just(Result(404, ""))

    private fun ResponseProvider.delayedBy(seconds: Duration): ResponseProvider =
        IO.sleep(seconds)
            .followedBy(this)


    private fun providerFor(req: HttpRequest): ResponseProvider =
        when (req.uri) {
            "/some/valid/path" -> someValidPath
            "/another/valid/path" -> anotherValidPath
            "/broken/path" -> brokenPath
            "/resource/which/takes/1s/to/complete" -> anotherValidPath.delayedBy(1.seconds)
            "/resource/which/takes/3s/to/complete" -> anotherValidPath.delayedBy(3.seconds)
            else -> notFound
        }


    fun handle(httpRequest: HttpRequest): HttpResponse =
        providerFor(httpRequest)
            .redeem(
                { t -> HttpResponse(httpRequest, 500, t.localizedMessage) },
                { result -> HttpResponse(httpRequest, result.first, result.second) }
            )
            .unsafeRunTimed(2.seconds)
            .getOrElse { HttpResponse(httpRequest, 503, "Processing timed-out") }


}
