package com.benjishults.bitnots.ingest.http

import com.benjishults.bitnots.config.Configuration
import com.benjishults.bitnots.ingest.routes.EndpointConfig
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.*
import org.springframework.beans.factory.*
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer
import reactor.ipc.netty.tcp.BlockingNettyContext
import javax.annotation.PostConstruct

class WebConfig(
        val port: Int = System.getProperty("port")
                ?.toString()
                ?.toInt() ?: 8080)
    : Configuration() {

    private lateinit var httpHandler: HttpHandler

    private lateinit var server: HttpServer

    private var nettyContext: BlockingNettyContext? = null
    
    // TODO separate the server, handler, and nettyContext into beans

    @PostConstruct
    override fun afterPropertiesSet() {
        super.afterPropertiesSet()
        server = HttpServer.create(port)
        httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
    }

    override fun beans(context: AnnotationConfigApplicationContext) =
            with(context) {
                registerBean<EndpointConfig>()
                registerBean("webHandler") {
                    RouterFunctions.toWebHandler(
                            context.getBean<RouterFunction<*>>("firstRouter"),
                            HandlerStrategies.withDefaults())
                }
            }
//            beans {
//                bean<EndpointConfig>()
//                bean("webHandler") {
//                    RouterFunctions.toWebHandler(
//                            ref<RouterFunction<*>>("firstRouter"),
//                            HandlerStrategies.withDefaults())
//                }
//            }

    fun start() {
        nettyContext = server.start(ReactorHttpHandlerAdapter(httpHandler))
    }

    fun startAndAwait() {
        server.startAndAwait(
                ReactorHttpHandlerAdapter(httpHandler),
                {
                    nettyContext = it
                }
        )
    }

    fun stop() {
        nettyContext?.shutdown()
    }

}
