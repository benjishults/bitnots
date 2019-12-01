package com.benjishults.bitnots.dataAccess.formula

import com.benjishults.bitnots.dataAccess.dao.FormulaDao
import com.benjishults.bitnots.model.terms.*
import com.benjishults.bitnots.model.terms.Function
import java.sql.Connection
import java.sql.Statement

class FormulaDaoImpl(val connectionFactory: () -> Connection) : FormulaDao {

    override fun <T : TermConstructor> insertTerm(term: Term<T>) {
        connectionFactory().use { connection ->
            connection.autoCommit = false
            insertTermRecursively(connection, term)
        }
    }

    fun findTermById(id: Long): Term<*> {
        connectionFactory().use { connection ->
            val consInfo = connection.prepareStatement(
                """
select * 
from term_fact tf
join term_cons_dim tcd on tf.term_cons_id = tcd.term_cons_id
where term_fact_id = ?
            """
            ).use {
                it.run {
                    setLong(1, id)
                    executeQuery().use {
                        it.run {
                            if (next())
                                object {
                                    val cons = getString("term_cons")
                                    val arity = getInt("arity")
                                    val bound = getShort("bound")
                                } else null
                        }
                    }
                }?.run {
                    if (arity > 0) {
                        if (bound > 0) {
                            TODO()
                        } else {
                            connection.prepareStatement(
                                """
select * 
from bound_var_fact bvf 
where bvf.parent_term_fact = ?
"""
                            ).use {
                                it.run {
                                    setLong(1, id)
                                    executeQuery().use {
                                        it.run {
                                            generateSequence {
                                                if (next()) {
                                                    object {
                                                        val bvCons = getString("bv_cons")
                                                        val position = getInt("position")
                                                    }
                                                } else {
                                                    null
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        } else {

                        }
                    }
                }
            }
        }

        private fun <T : TermConstructor> insertTermRecursively(
            connection: Connection,
            term: Term<T>,
            termParentId: Long = -1,
            formParentId: Long = -1,
            position: Int = 0
        ): Boolean {
            return when (term) {
                is BoundVariable -> {
                    term.save(connection, termParentId, formParentId, position)
                    true
                }
                is FreeVariable -> {
                    term.save(connection, termParentId, formParentId, position)
                    true
                }
                is Function -> {
                    val termConsKey =
                        connection.prepareStatement(
                            """
insert into term_cons_dim (term_cons, arity)
values (?, ?)
on conflict do nothing
""",
                            Statement.RETURN_GENERATED_KEYS
                        ).use {
                            it.apply {
                                setString(1, term.cons.name)
                                setInt(2, term.cons.arity)
                                executeUpdate()
                            }.generatedKeys.run {
                                if (next())
                                    getLong(1)
                                else
                                    -1L
                            }
                        }
                    if (termConsKey >= 0) {
                        val newParentId =
                            connection.prepareStatement(
                                """
insert into term_fact (term_cons_id, parent_term_fact, parent_form_fact, position)
values (?, ?, ?, ?)
on conflict do nothing
""",
                                Statement.RETURN_GENERATED_KEYS
                            ).use {
                                it.apply {
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

        private fun save(
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
on conflict do nothing
"""
            ).use {
                it.apply {
                    setString(1, value)
                    setLong(2, termParentId)
                    setLong(3, formParentId)
                    setInt(4, position)
                    execute()
                }
            }
        }

        private fun BoundVariable.save(
            connection: Connection,
            termParentId: Long,
            formParentId: Long,
            position: Int
        ) {
            save(connection, "bound_var_fact", "bv_cons", cons.name, termParentId, formParentId, position)
        }

        private fun FreeVariable.save(
            connection: Connection,
            termParentId: Long,
            formParentId: Long,
            position: Int
        ) {
            save(connection, "free_var_fact", "fv_cons", cons.name, termParentId, formParentId, position)
        }

    }
