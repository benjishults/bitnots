package com.benjishults.bitnots.dataAccess.term

import com.benjishults.bitnots.model.terms.Function
import java.sql.Connection
import java.sql.Statement

class PersistentFunction(val term: Function) : PersistentTerm {
    override fun save(connection: Connection, termParentId: Long, formParentId: Long, position: Int) {
        val termConsKey =
            connection.prepareStatement(
                """
insert into term_cons_dim (term_cons, arity) values (?, ?) on conflict do nothing
""",
                Statement.RETURN_GENERATED_KEYS
            ).apply {
                setString(1, term.cons.name)
                setInt(2, term.cons.arity)
                executeUpdate()
            }.generatedKeys.run {
                if (next())
                    getLong(1)
                else
                    -1L
            }
        if (termConsKey >= 0) {
            val newParentId =
                connection.prepareStatement(
                    """
insert into term_fact (term_cons_id, parent_term_fact, parent_form_fact, position) values (?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
                ).apply {
                    setLong(1, termConsKey)
                    setLong(3, termParentId)
                    setLong(4, formParentId)
                    setInt(5, position)
                    executeUpdate()
                }.generatedKeys.run {
                    if (next())
                        getLong(1)
                    else
                        -1L
                }
            if (newParentId >= 0) {
                term.arguments.withIndex().all { (index, subTerm) ->
                    insertTermRecursively(connection, subTerm, newParentId, -1, index + 1)
                }
            } else false
        } else false
    }
    else -> {
        false
    }
}
    }
}