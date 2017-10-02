package com.benjishults.bitnots.dataAccess.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import java.sql.Driver
import java.sql.DriverManager
import java.util.Properties

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "\${config}/db.properties")
//@PropertySource("classpath:db.properties")
class DateSourceConfig {

    init {
        DriverManager.registerDriver(Class.forName("org.postgresql.Driver").newInstance() as Driver)
    }
    
    @Value("\${jdbc.url}")
    lateinit var url: String
    
    @Bean
    fun dbProperties() : Properties {
        return Properties().apply {
            put("ssl", "true")
            put("user", "postgres")
            put("password", "admin")
        }
    }

}