package com.benjishults.bitnots.proofService.context

import com.benjishults.bitnots.proofService.context.model.ProvingContext
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.util.model.Identifiable
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.apache.camel.builder.RouteBuilder
import java.util.*

class ContextRoutes : RouteBuilder() {
    override fun configure() {
        // from("jetty:http://localhost:8989/api/context")
    }
}

fun Routing.contextRouting(): Unit {
    route("/api/context") {
        post("") { descriptor: TptpProblemFileDescriptor ->
            val contextId = UUID.randomUUID().toString()
            call.respond(HttpStatusCode.Created, object : Identifiable {
                override val id = contextId
            })
        }
        get("{contextId}") {
            val contextId = call.parameters["contextId"]!!
            call.respond(ProvingContext(contextId))
        }
    }
}
