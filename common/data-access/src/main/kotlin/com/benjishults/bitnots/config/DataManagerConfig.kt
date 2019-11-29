package com.benjishults.bitnots.config

import com.benjishults.bitnots.dataAccess.dao.ProblemDao
import com.benjishults.bitnots.dataAccess.dao.impl.ProblemDaoImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import java.sql.Driver
import java.sql.DriverManager

@Configuration
@Import(PropertiesConfig::class)
@PropertySource(/*ignoreResourceNotFound = true,*/ value = "file:\${config}/db.properties")
open class DataManagerConfig {

    init {
        DriverManager.registerDriver(Class.forName("org.postgresql.Driver").newInstance() as Driver)
    }

    @Value("\${jdbc.url}")
    lateinit var url: String

    @Bean
    open fun problemDao(): ProblemDao =
            ProblemDaoImpl(url)

}
