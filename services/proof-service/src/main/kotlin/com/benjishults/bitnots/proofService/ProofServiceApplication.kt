package com.benjishults.bitnots.proofService

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.apache.camel.spring.javaconfig.Main

@Configuration
@Import()
class ProofServiceApplication

fun main(args: Array<String>) {
    Main.main(*args)
}
