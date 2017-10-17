package com.benjishults.bitnots.ingest.http

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer
import javax.annotation.PostConstruct

@Configuration
class HttpConfig : WebFluxConfigurer {

    @Autowired
    private lateinit var context: ApplicationContext

    @PostConstruct
    fun httpHandler() {
        val adapter = ReactorHttpHandlerAdapter(WebHttpHandlerBuilder.applicationContext(context).build());
        HttpServer.create("localhost", 8078).newHandler(adapter).block();
    }

}