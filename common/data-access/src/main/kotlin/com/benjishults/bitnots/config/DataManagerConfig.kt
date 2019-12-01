package com.benjishults.bitnots.config

import com.benjishults.bitnots.dataAccess.formula.FormulaDaoImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import javax.annotation.PostConstruct

@Configuration
@Import(PropertiesConfig::class)
@PropertySource(/*ignoreResourceNotFound = true,*/ "file:\${config}/db.properties")
class DataManagerConfig {

    @Value("\${jdbc.url}")
    lateinit var url: String
    @Value("\${jdbc.driver}")
    lateinit var driverClass: String

    @PostConstruct
    fun afterPropertiesSet() {
        DriverManager.registerDriver(Class.forName(driverClass).getConstructor().newInstance() as Driver)
    }

    val connectionFactory : () -> Connection = {
        DriverManager.getConnection(url)
    }

    @Bean
    fun formulaDao() {
        FormulaDaoImpl(connectionFactory)
    }

}
