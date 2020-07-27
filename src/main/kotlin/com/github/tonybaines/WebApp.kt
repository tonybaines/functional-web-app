package com.github.tonybaines

import com.github.tonybaines.MaybeValid.Invalid
import com.github.tonybaines.MaybeValid.Valid

object WebApp {

    private val someValidPath: ResponseProvider = { req -> HttpResponse(req, 200, "Hello World!") }
    private val anotherValidPath: ResponseProvider = { req -> HttpResponse(req, 200, "42") }
//    private val brokenPath: ResponseProvider = { ItFailed(BadThing.ExceptionallyBadThing(RuntimeException("Whoops!"))) }

//    private val notFound: ResponseProvider = { ItWorked(Result.Yup(404, "")) }

//    private fun ResponseProvider.delayedBy(seconds: Duration): ResponseProvider =
//        IO.sleep(seconds)
//            .followedBy(this)


    private fun match(req: HttpRequest): (HttpRequest) -> MaybeValid =
        when (req.uri) {
            "/some/valid/path" -> { r -> Valid(someValidPath) }
            "/another/valid/path" -> { r -> Valid(anotherValidPath) }
//            "/broken/path" -> brokenPath
//            "/resource/which/takes/1s/to/complete" -> anotherValidPath//.delayedBy(1.seconds)
//            "/resource/which/takes/3s/to/complete" -> anotherValidPath//.delayedBy(3.seconds)
            else -> { r -> Invalid.NotFound() }
        }

    private fun execute(httpRequest: HttpRequest, handler: (HttpRequest) -> MaybeValid): HttpResponse {
        val handler = handler(httpRequest)
        return when(handler) {
            is Valid -> handler.handler(httpRequest)
            is Invalid -> HttpResponse(httpRequest, handler.status, handler.reason)
        }
    }

    fun handle(httpRequest: HttpRequest): HttpResponse =
        execute(httpRequest,
            match(httpRequest)
        )

}

//private fun ResponseProvider.handleTheResult(
//    whenAGoodThingHappens: (ItWorked) -> HttpResponse,
//    whenABadThingHappens: (ItFailed) -> HttpResponse): ResponseProvider = {
//
//    when() {
//        is ItFailed -> { thing: ItFailed -> whenABadThingHappens(thing) }
//        is ItWorked -> { thing: ItWorked -> whenAGoodThingHappens(thing) }
//    }
//}
//
//private fun ResponseProvider.getOrElse(defaultError: () -> HttpResponse): HttpResponse {
//    val result = this()
//    return when (result) {
//        is ItWorked -> result.result
//        is ItFailed -> defaultError()
//    }
//}
