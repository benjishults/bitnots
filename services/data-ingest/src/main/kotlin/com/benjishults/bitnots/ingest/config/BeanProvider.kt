package com.benjishults.bitnots.ingest.config

import org.springframework.context.support.BeanDefinitionDsl

interface BeanProvider {
    fun beans(): BeanDefinitionDsl
}

interface ContextInitializer {
    fun init()
}

