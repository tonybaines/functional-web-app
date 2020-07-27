package com.github.tonybaines

import com.github.tonybaines.MaybeValid.Invalid
import com.github.tonybaines.MaybeValid.Valid
import com.github.tonybaines.Outcome.Bad
import com.github.tonybaines.Outcome.Good
import java.time.Instant
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
object WebApp {
    private fun log(msg: String) {
        System.err.println("[${Instant.now()}] $msg")
    }

    private val executorService = Executors.newSingleThreadExecutor()

    private val someValidPath: ResponseProvider = { req -> Good(200, "Hello World!") }
    private val anotherValidPath: ResponseProvider = { req -> Good(200, "42") }
    private val brokenPath: ResponseProvider =
        { req -> (Bad.ExceptionallyBad(500, RuntimeException("Whoops!"))) }

    private fun ResponseProvider.delayedBy(seconds: Duration): ResponseProvider = { req ->
        log("Delayed operation")
        Thread.sleep(seconds.toLongMilliseconds())
        log("Delay done")
        this(req)
    }


    /**
     * Lookup a handler for the request, if available
     */
    private fun match(req: HttpRequest): (HttpRequest) -> MaybeValid =
        when (req.uri) {
            "/some/valid/path" -> { r -> Valid(someValidPath) }
            "/another/valid/path" -> { r -> Valid(anotherValidPath) }
            "/valid/path/with/params" ->
                if (req.params.containsKey("age") && req.params["age"] is Int) { r -> Valid(anotherValidPath) }
                else { _ -> Invalid.BadRequest() }
            "/broken/path" -> { r -> Valid(brokenPath) }
            "/resource/which/takes/1s/to/complete" -> { r -> Valid(anotherValidPath.delayedBy(1.seconds)) }
            "/resource/which/takes/3s/to/complete" -> { r -> Valid(anotherValidPath.delayedBy(3.seconds)) }
            else -> { r -> Invalid.NotFound() }
        }


    /**
     * Evaluation happens here, if required
     */
    private fun execute(httpRequest: HttpRequest, handler: (HttpRequest) -> MaybeValid): Outcome {
        log("Looking up handler")
        return when (val resource = handler(httpRequest)) {
            is Valid -> {
                log("Invoking handler")
                executorService
                    .submit(Callable { resource.provider(httpRequest)})
                    .runCatching { get(2, TimeUnit.SECONDS) }
                    .getOrElse { _ -> Bad.JustBad(503, "Timed-out") }

            }
            is Invalid -> Bad.JustBad(resource.status, resource.reason)
        }
    }

    private fun decode(req: HttpRequest, outcome: Outcome): HttpResponse =
        when (outcome) {
            is Good -> HttpResponse(req, outcome.status, outcome.body)
            is Bad -> HttpResponse(req, outcome.status, outcome.reason)
        }

    /**
     * Entrypoint
     */
    fun handle(httpRequest: HttpRequest): HttpResponse =
        decode(httpRequest,
            execute(httpRequest,
                match(httpRequest) // (HttpRequest) -> MaybeValid
            ) // Outcome
        ) // HttpResponse

}

