package com.benjishults.bitnots.dataAccess.dao.impl

import com.benjishults.bitnots.dataAccess.dao.BaseDao
import java.sql.Connection
import java.sql.DriverManager

open class BaseDaoImpl(val url: String) : BaseDao {

    fun getConnection(): Connection =
            DriverManager.getConnection(url)

}
