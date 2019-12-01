package com.benjishults.bitnots.dataAccess.term

import java.sql.Connection

interface PersistentTerm {

    fun save(
        connection: Connection,
        termParentId: Long = -1,
        formParentId: Long = -1,
        position: Int = 0
    )

}
