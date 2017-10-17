package com.benjishults.bitnots.ingest

import com.benjishults.bitnots.ingest.http.WebConfig
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer
import reactor.ipc.netty.tcp.BlockingNettyContext


class IngestApplication {

    private val httpHandler: HttpHandler

    private val server: HttpServer

    private var nettyContext: BlockingNettyContext? = null

    constructor() {
        val port = System.getProperty("port")?.toString()?.toInt() ?: 8080

        val context = GenericApplicationContext().apply {
            beans().initialize(this)
            refresh()
        }

        server = HttpServer.create(port)
        httpHandler = WebHttpHandlerBuilder.applicationContext(context).build()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Thread.sleep(20000) // TODO get rid of this... waiting for debugger to attach
            IngestApplication().startAndAwait()
        }
    }

    fun beans() = beans {
        bean<WebConfig>()
    }

    fun start() {
        nettyContext = server.start(ReactorHttpHandlerAdapter(httpHandler))
    }

    fun startAndAwait() {
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler), { nettyContext = it })
    }

    fun stop() {
        nettyContext?.shutdown()
    }

}

