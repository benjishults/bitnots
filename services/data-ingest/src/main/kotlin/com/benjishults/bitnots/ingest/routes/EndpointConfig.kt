package com.benjishults.bitnots.ingest.routes

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class EndpointConfig { // : Configuration() {

    class MyHandler {
        fun getMessages(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().body(Mono.just("Hello"), String::class.java)

        fun addMessage(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().body(Mono.just("You posted?"), String::class.java)

        fun getMessage(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.ok().body(Mono.just(req.pathVariable("id")), String::class.java)

        fun updateMessage(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                TODO()

        fun deleteMessage(@Suppress("UNUSED_PARAMETER") req: ServerRequest): Mono<ServerResponse> =
                ServerResponse.badRequest().build()
    }

    val messageHandler = MyHandler()

    fun routes() =
            router {
                (accept(MediaType.TEXT_PLAIN) and "/ingest").nest {
                    GET("/", messageHandler::getMessages)
                    POST("/{}", messageHandler::addMessage)
                    GET("/{id}", messageHandler::getMessage)
                    PUT("/{id}", messageHandler::updateMessage)
                    DELETE("/{id}", messageHandler::deleteMessage)
                }
            }
}
