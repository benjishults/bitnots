package com.benjishults.bitnots.proofService.context

import org.apache.camel.builder.RouteBuilder

class ContextRoutes: RouteBuilder() {
    override fun configure() {
        // from("jetty:http://localhost:8989/api/context")
    }
}
