package com.benjishults.bitnots.ingest.routes

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class EndpointConfig {

    class MyHandler {
        fun getMessages(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().build()

        fun addMessage(@Suppress("UNUSED_PARAMETER")req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().build()

        fun getMessage(@Suppress("UNUSED_PARAMETER")req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().build()

        fun updateMessage(@Suppress("UNUSED_PARAMETER")req: ServerRequest): Mono<ServerResponse> =
                TODO()

        fun deleteMessage(@Suppress("UNUSED_PARAMETER")req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.badRequest().build()
    }

    val messageHandler = MyHandler()

    @Bean
    fun apis() =
            router {
                (accept(MediaType.APPLICATION_JSON) and "/messages").nest {
                    GET("/", messageHandler::getMessages)
                    POST("/", messageHandler::addMessage)
                    GET("/{id}", messageHandler::getMessage)
                    PUT("/{id}", messageHandler::updateMessage)
                    DELETE("/{id}", messageHandler::deleteMessage)
                }
            }
}
