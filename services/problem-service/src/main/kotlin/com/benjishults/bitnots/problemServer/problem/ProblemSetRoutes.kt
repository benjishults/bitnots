package com.benjishults.bitnots.problemServer.problem

import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.util.identity.Identified
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import java.util.*

fun Routing.problemSetRouting(): Unit {
    route("/api/problem-set") {
        post("") { descriptor: TptpProblemFileDescriptor ->
            val contextId = UUID.randomUUID().toString()
            call.respond(HttpStatusCode.Created, object : Identified {
                override val id = contextId
            })
        }
        get("{problemSetId}") {
            val contextId = call.parameters["problemSetId"]!!
            call.parameters["format"]
            call.respond(contextId)
        }
    }
}
