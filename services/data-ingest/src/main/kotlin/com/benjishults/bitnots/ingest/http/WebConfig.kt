package com.benjishults.bitnots.ingest.http

import com.benjishults.bitnots.config.Configuration
import com.benjishults.bitnots.ingest.routes.EndpointConfig
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.RouterFunctions
import javax.annotation.PostConstruct

class WebConfig : Configuration() {

    override fun beans(): BeanDefinitionDsl =
            beans {
                bean<EndpointConfig>() // { EndpointConfig(context) }
                bean("webHandler") {
                    RouterFunctions.toWebHandler(
                            ref<EndpointConfig>().routes(),
                            HandlerStrategies.withDefaults())
                }
            }

}
