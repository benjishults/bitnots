package com.benjishults.bitnots.ingest

import com.benjishults.bitnots.ingest.http.WebConfig
import com.benjishults.bitnots.ingest.routes.EndpointConfig
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.context.support.*
import org.springframework.beans.factory.*
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer
import reactor.ipc.netty.tcp.BlockingNettyContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext


class IngestApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Thread.sleep(20000) // TODO get rid of this... waiting for debugger to attach
            IngestApplication()
        }
    }

    constructor() {
        val context = AnnotationConfigApplicationContext().apply {
            registerBean<WebConfig>()
//            beans().initialize(this)
            refresh()
        }
        context.getBean<WebConfig>().startAndAwait()  // getBean<WebConfig>().st
    }

//    fun beans() = beans {
//        bean<WebConfig>()
//    }

}

