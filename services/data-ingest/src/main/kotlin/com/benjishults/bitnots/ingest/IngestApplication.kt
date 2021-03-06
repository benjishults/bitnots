package com.benjishults.bitnots.ingest

import com.benjishults.bitnots.ingest.http.HttpConfig
import com.benjishults.bitnots.ingest.routes.EndpointConfig
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.config.EnableWebFlux


@Configuration
@EnableWebFlux
@Import(EndpointConfig::class, HttpConfig::class)
class IngestApplication : CommandLineRunner {
    override fun run(vararg args: String) {
        Thread.currentThread().join()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(IngestApplication::class.java, *args)
}
