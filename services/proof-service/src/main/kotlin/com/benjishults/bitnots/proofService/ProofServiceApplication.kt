package com.benjishults.bitnots.proofService

import com.benjishults.bitnots.proofService.context.contextRouting
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.routing.Routing
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json

class ProofServiceApplication

@KtorExperimentalAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, 8888) {
        install(ContentNegotiation) {
            serialization(
                    contentType = ContentType.Application.Json,
                    json = Json(DefaultJsonConfiguration))
        }
        routing(Routing::contextRouting)
    }.start(true)
}
