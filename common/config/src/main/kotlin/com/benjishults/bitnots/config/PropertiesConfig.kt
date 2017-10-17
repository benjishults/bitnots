package com.benjishults.bitnots.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
open class PropertiesConfig {

    companion object {
        @Bean
        @JvmStatic
        fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
            return PropertySourcesPlaceholderConfigurer()
        }
    }

}
