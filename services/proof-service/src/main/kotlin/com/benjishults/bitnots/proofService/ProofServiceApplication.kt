package com.benjishults.bitnots.proofService

import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.util.model.Identifiable
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.Json
import java.util.*

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

    // HttpConfig
    // CamelConfig.context.addRoutes(KeepAlive())
    // CamelConfig.context.addRoutes(KeepAlive())
    // CamelConfig.context.start()
}

fun Routing.contextRouting(): Unit {
            post("/api/context") {  descriptor: TptpProblemFileDescriptor ->
                val contextId = UUID.randomUUID().toString()
                call.respond(HttpStatusCode.Created, object : Identifiable {
                    override val id = contextId
                })
            }
            get("/api/context/{contextId}") {
                val contextId = call.parameters["contextId"]!!
                call.respond(object : Identifiable {
                    override val id = contextId
                })
            }

}
