package com.github.tonybaines

import com.github.tonybaines.Method.GET
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@ExperimentalTime
class WebAppSpec : StringSpec({
    "Accepts a valid request for static data and returns a response" {
        val httpRequest = HttpRequest(GET, "/some/valid/path")
        WebApp.handle(httpRequest) shouldBe HttpResponse(httpRequest, body = "Hello World!")
    }

    "Handle a request for a different valid path" {
        WebApp.handle(HttpRequest(GET, "/another/valid/path")).body shouldBe "42"
    }

    "Handle a request for an unknown path" {
        WebApp.handle(HttpRequest(GET, "/unknown/path")).status shouldBe 404
    }

    "Handle a request that can fail" {
        WebApp.handle(HttpRequest(GET, "/broken/path")).status shouldBe 500
    }

    "Handle a request with latency" {
        WebApp.handle(HttpRequest(GET, "/resource/which/takes/1s/to/complete"))
            .body shouldBe "42"
    }

    "Handle a request with latency that fails" {
        WebApp.handle(HttpRequest(GET, "/resource/which/takes/3s/to/complete"))
            .status shouldBe 503
    }
})