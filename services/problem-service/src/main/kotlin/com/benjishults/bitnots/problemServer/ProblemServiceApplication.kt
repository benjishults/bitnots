package com.benjishults.bitnots.problemServer

import com.benjishults.bitnots.problemServer.problem.problemSetRouting
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.routing.Routing
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    embeddedServer(Netty, 8088) {
        install(ContentNegotiation) {
            serialization(
                    contentType = ContentType.Application.Json,
                    json = Json(DefaultJsonConfiguration))
        }
        routing(Routing::problemSetRouting)
    }.start(true)
}
