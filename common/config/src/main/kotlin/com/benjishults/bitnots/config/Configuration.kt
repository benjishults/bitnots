package com.benjishults.bitnots.config

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import javax.annotation.PostConstruct

abstract class Configuration : ApplicationContextAware {

    protected lateinit var context: GenericApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext as GenericApplicationContext
//        beans().initialize(context)
    }

    /**
     * implementors should probably call super.  This iniitailizes the beans in the context.
     */
    @PostConstruct
    open fun afterPropertiesSet() {
        beans().initialize(context)
    }

    abstract fun beans(): BeanDefinitionDsl

}