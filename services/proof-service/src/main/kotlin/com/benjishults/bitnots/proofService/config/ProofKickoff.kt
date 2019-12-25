package com.benjishults.bitnots.proofService.control

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
class ProofKickoff : SingleRouteCamelConfiguration() {
    override fun route() = object : RouteBuilder() {
        override fun configure() {
            from("timer:start?fixedRate=true&period=5000")
                    .process { exchange ->
                        println("route started")
                    }
                    .to("mock:stop?retainLast=1")
        }
    }
}
