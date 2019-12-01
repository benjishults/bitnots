package com.benjishults.bitnots.dataAccess.term

import com.benjishults.bitnots.model.terms.Variable
import java.sql.Connection

abstract class PersistentVariable<T:Variable<*>>(val variable: T) : PersistentTerm {

    protected fun saveHelper(
        connection: Connection,
        table: String,
        column: String,
        value: String,
        termParentId: Long,
        formParentId: Long,
        position: Int
    ) {
        connection.prepareStatement(
            """
insert into ${table} (${column}, parent_term_fact, parent_form_fact, position)
values (?, ?, ?, ?)
"""
        ).apply {
            setString(1, value)
            setLong(2, termParentId)
            setLong(3, formParentId)
            setInt(4, position)
            execute()
        }

    }

}