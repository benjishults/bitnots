package com.benjishults.bitnots.dataAccess.formula

import com.benjishults.bitnots.dataAccess.dao.FormulaDao
import com.benjishults.bitnots.dataAccess.dao.impl.BaseDaoImpl
import com.benjishults.bitnots.model.terms.*
import com.benjishults.bitnots.model.terms.Function
import org.postgresql.core.ConnectionFactory
import java.sql.Connection
import java.sql.Statement
import javax.swing.plaf.nimbus.State

class FormulaDaoImpl(url: String) : BaseDaoImpl(url), FormulaDao {


    override fun <T : TermConstructor> insertTerm(term: Term<T>) {
        getConnection().use { connection ->
            insertTermRecursively(connection, term)
        }
    }

    private fun <T : TermConstructor> insertTermRecursively(
        connection: Connection,
        term: Term<T>,
        termParentId: Long = -1,
        formParentId: Long = -1,
        position: Int = 0
    ): Boolean {
        connection.autoCommit = false
        return when (term) {
            is BoundVariable -> {
                connection.prepareStatement(
                    """
                          insert into bound_var_fact (bv_cons, position)
                        """.trimIndent(),
                    Statement.RETURN_GENERATED_KEYS
                )
                connection.createStatement().use {
                    it.execute(
                        """
                          insert into bound_var_fact (bv_cons, position
                      """.trimIndent()
                    )
                }
                true
            }
            is FreeVariable -> {
                true
            }
            is Function -> {
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
