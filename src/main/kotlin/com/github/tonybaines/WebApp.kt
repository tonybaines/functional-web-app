package com.github.tonybaines

object WebApp {

    private val someValidPath: ResponseProvider = { req -> HttpResponse(req,200, "Hello World!") }
    private val anotherValidPath: ResponseProvider = { req -> HttpResponse(req,200, "42") }
//    private val brokenPath: ResponseProvider = { ItFailed(BadThing.ExceptionallyBadThing(RuntimeException("Whoops!"))) }

//    private val notFound: ResponseProvider = { ItWorked(Result.Yup(404, "")) }

//    private fun ResponseProvider.delayedBy(seconds: Duration): ResponseProvider =
//        IO.sleep(seconds)
//            .followedBy(this)


    private fun providerFor(req: HttpRequest): ResponseProvider = someValidPath
//        when (req.uri) {
//            "/some/valid/path" -> someValidPath
//            "/another/valid/path" -> anotherValidPath
//            "/broken/path" -> brokenPath
//            "/resource/which/takes/1s/to/complete" -> anotherValidPath//.delayedBy(1.seconds)
//            "/resource/which/takes/3s/to/complete" -> anotherValidPath//.delayedBy(3.seconds)
//            else -> notFound
//        }


    fun handle(httpRequest: HttpRequest): HttpResponse =
        providerFor(httpRequest)(httpRequest)

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
