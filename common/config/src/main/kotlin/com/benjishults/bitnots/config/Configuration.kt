package com.benjishults.bitnots.config

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.BeanDefinitionDsl
import javax.annotation.PostConstruct

abstract class Configuration : ApplicationContextAware {

    protected lateinit var context: AnnotationConfigApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext as AnnotationConfigApplicationContext
    }

    /**
     * implementors should probably call super.  This iniitailizes the beans in the context.
     */
    @PostConstruct
    open fun afterPropertiesSet() {
        beans(context)
    }

    abstract fun beans(context: AnnotationConfigApplicationContext)

}