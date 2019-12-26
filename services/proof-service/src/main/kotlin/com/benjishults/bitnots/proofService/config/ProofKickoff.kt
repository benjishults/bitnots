package com.benjishults.bitnots.proofService.control

import org.apache.camel.builder.RouteBuilder

class ProofKickoff : RouteBuilder() {
        override fun configure() {
            from("timer:start?fixedRate=true&period=5000")
                    .process { exchange ->
                        println("route started")
                    }
                    .to("mock:stop?retainLast=1")
        }
}
