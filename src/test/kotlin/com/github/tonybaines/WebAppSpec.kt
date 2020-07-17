package com.github.tonybaines

import com.github.tonybaines.Method.GET
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

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
})