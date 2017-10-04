package com.benjishults.bitnots.dataAccess.dao.impl

import com.benjishults.bitnots.dataAccess.dao.ProblemDao
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.sexpParser.SexpParser
import com.benjishults.bitnots.sexpParser.SexpTokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.theory.problem.Problem
import com.benjishults.bitnots.theory.problem.ProblemSource
import org.apache.commons.codec.digest.DigestUtils
import org.postgresql.util.PSQLException
import java.sql.PreparedStatement

class ProblemDaoImpl(url: String, val batchSize: Int = 50) : BaseDaoImpl<Problem>(url), ProblemDao {

    companion object {
        const val INSERT_FORMULA = """
 insert into formula_dim
 (formula, formula_hash)
 values (?, ?)
 on conflict do nothing
"""

        const val INSERT_FORMULA_PLUS = """
 insert into formula_plus_dim
 (formula_name, formula_role, formula_hash, problem_id) values
 (?, ?, ?, ?)
"""

        fun prepareInsertFormula(formula: String, pf: PreparedStatement) {
            pf.setString(1, formula)
            pf.setString(2, DigestUtils.md5Hex(formula))
        }

        fun prepareInsertFormulaPlus(name: String, role: FormulaRoles, formula: String, problemId: Long, pf: PreparedStatement) {
            pf.setString(1, name)
            pf.setString(2, role.name)
            pf.setString(3, DigestUtils.md5Hex(formula))
            pf.setLong(4, problemId);
        }

        fun <T> getIds(it: PreparedStatement, id: String): Collection<T> =
                mutableListOf<T>().apply {
                    it.getGeneratedKeys().use {
                        while (it.next()) {
                            @Suppress("UNCHECKED_CAST")
                            add(it.getObject(id) as T)
                        }
                    }
                }
    }

    override fun insertProblem(problem: Problem): Long =
            getConnection().use {
                connection ->
                try {
                    // insert problem
                    connection.prepareStatement("""
 insert into problem_dim
 (problem_source, problem_source_detail)
 values (?, ?)
 returning problem_id
"""
                    ).use {
                        it.setString(1, problem.source.name)
                        it.setString(2, problem.sourceDetail)
                        it.executeQuery().use {
                            it.next()
                            it.getLong("problem_id")
                        }
                    }
                } catch(e: PSQLException) {
                    getIdBySource(problem.source, problem.sourceDetail)
                }.also { problemId ->
                    // insert formulas and formula_plus
                    val formulaPlusIds = mutableListOf<Long>()
                    connection.prepareStatement(INSERT_FORMULA).use { insertFormula ->
                        connection.prepareStatement(INSERT_FORMULA_PLUS, arrayOf("formula_plus_id")).use { insertFormulaPlus ->
                            @Suppress("UNCHECKED_CAST")
                            ((problem.axioms
                                    + problem.conjectures
                                    + problem.hypotheses)
                                    as List<FolAnnotatedFormula>).forEach { annotatedFormula ->
                                annotatedFormula.formula.toString().let { formula ->

                                    prepareInsertFormula(formula, insertFormula)
                                    prepareInsertFormulaPlus(annotatedFormula.name, annotatedFormula.formulaRole, formula, problemId, insertFormulaPlus)

                                    insertFormula.execute()
                                    try {
                                        insertFormulaPlus.execute()
                                        null
                                    } catch (e: PSQLException) {
                                        getIdByAnnotatedFormula(annotatedFormula, problemId)
                                    }?.let {
                                        formulaPlusIds.add(it)
                                    } ?: formulaPlusIds.add(insertFormulaPlus.getGeneratedKeys().run {
                                        next()
                                        getLong("formula_plus_id")
                                    })
                                }
                            }
                        }
                    }
                    connection.prepareStatement("""
 insert into problem_formula_plus
 (formula_plus_id, problem_id) values
 (?, ?)
 on conflict do nothing
"""
                    ).use {
                        formulaPlusIds.forEachIndexed { batchCount, fPlus ->
                            it.setLong(1, fPlus)
                            it.setLong(2, problemId)
                            it.addBatch()
                            if (batchCount + 1 % batchSize == 0) {
                                it.executeBatch()
                            }
                        }
                        it.executeBatch()
                    }
                }
            }

    override fun insertAnnotatedFormula(annotatedFormula: FolAnnotatedFormula): Long =
            getConnection().use { connection ->
                connection.prepareStatement(INSERT_FORMULA).use {
                    annotatedFormula.formula.toString().let { string ->
                        it.setString(1, string)
                        it.setString(2, string)
                        it.setString(3, string)
                    }
                    it.executeBatch()
                    it.executeQuery().use {
                        it.next()
                        it.getLong("formula_hash")
                    }.let { id ->
                        connection.prepareStatement("""
 insert into formula_plus_dim
 (formula_name, formula_role, formula_hash) values
 (?, ?, ?)
 returning formula_plus_id
"""
                                // gen_random_uuid() -- 
                                // where formula_plus.formula_id <> EXCLUDED.formula_id
                        ).use {
                            it.setString(1, annotatedFormula.name)
                            it.setString(2, annotatedFormula.formulaRole.name)
                            it.setLong(3, id)
                            it.executeQuery().use {
                                it.next()
                                it.getLong("formula_plus_id")
                            }
                        }
                    }
                }
            }

    override fun insertAxiomSet() {
        TODO()
    }

    override fun getIdByAnnotatedFormula(annotatedFormula: FolAnnotatedFormula, problemId: Long): Long =
            annotatedFormula.formula.toString().let { formula ->
                getConnection().use {
                    it.prepareStatement("""
 select fp.formula_plus_id
 from formula_plus_dim fp
 where fp.formula_hash = ?
 and fp.problem_id = ?
 and fp.formula_name = ?
 and fp.formula_role = ?
"""
                    ).use {
                        it.setString(1, DigestUtils.md5Hex(formula))
                        it.setLong(2, problemId)
                        it.setString(3, annotatedFormula.name)
                        it.setString(4, annotatedFormula.formulaRole.name)
                        it.executeQuery().use {
                            it.next()
                            it.getLong("formula_plus_id")
                        }
                    }
                }
            }


    override fun getIdBySource(source: ProblemSource, detail: String): Long =
            getConnection().use {
                it.prepareStatement("""
 select p.problem_id
 from problem_dim p
 where p.problem_source = ?
 and p.problem_source_detail = ?
"""
                ).use {
                    it.setString(1, source.name)
                    it.setString(2, detail)
                    it.executeQuery().use {
                        it.next()
                        it.getLong("problem_id")
                    }
                }
            }

    override fun getBySource(source: ProblemSource, detail: String): Problem {
        val conjectures = mutableListOf<AnnotatedFormula>()
        val axioms = mutableListOf<AnnotatedFormula>()
        val hypotheses = mutableListOf<AnnotatedFormula>()
        getConnection().use {
            it.prepareStatement("""
 select fp.formula_role, fp.formula_name, f.formula
 from problem_dim p
 natural join problem_axiom_set
 natural join axiom_set_dim
 natural join axiom_axiom_set
 natural join formula_plus_dim fp
 natural join formula_dim f
 where p.problem_source = ?
 and p.problem_source_detail = ?
"""
            ).use {
                it.setString(1, source.name)
                it.setString(2, detail)
                it.executeQuery().use {
                    while (it.next()) {
                        axioms.add(FolAnnotatedFormula(
                                it.getString("formula_name"),
                                FormulaRoles.valueOf(it.getString("formula_role")),
                                SexpParser.parseFormula(SexpTokenizer(it.getString("formula").reader().buffered(), "bitnots.formula.formula"), emptySet())))
                    }
                }
            }
            it.prepareStatement("""
 select fp.formula_role, fp.formula_name, f.formula
 from problem_dim p
 natural join problem_formula_plus pf 
 natural join formula_plus_dim fp
 natural join formula_dim f
 where p.problem_source = ?
 and p.problem_source_detail = ?
"""
            ).use {
                it.setString(1, source.name)
                it.setString(2, detail)
                it.executeQuery().use {
                    while (it.next()) {
                        FormulaRoles.valueOf(it.getString("formula_role")).let { role ->
                            when (role) {
                                FormulaRoles.axiom -> {
                                    axioms.add(FolAnnotatedFormula(
                                            it.getString("formula_name"),
                                            role,
                                            SexpParser.parseFormula(SexpTokenizer(it.getString("formula").reader().buffered(), "bitnots.formula.formula"), emptySet())))

                                }
                                FormulaRoles.hypothesis, FormulaRoles.assumption -> {
                                    hypotheses.add(FolAnnotatedFormula(
                                            it.getString("formula_name"),
                                            role,
                                            SexpParser.parseFormula(SexpTokenizer(it.getString("formula").reader().buffered(), "bitnots.formula.formula"), emptySet())))

                                }
                                FormulaRoles.conjecture -> {
                                    conjectures.add(FolAnnotatedFormula(
                                            it.getString("formula_name"),
                                            role,
                                            SexpParser.parseFormula(SexpTokenizer(it.getString("formula").reader().buffered(), "bitnots.formula.formula"), emptySet())))

                                }
                                FormulaRoles.negated_conjecture -> {
                                    conjectures.add(FolAnnotatedFormula(
                                            it.getString("formula_name"),
                                            role,
                                            Not(SexpParser.parseFormula(SexpTokenizer(it.getString("formula").reader().buffered(), "bitnots.formula.formula"), emptySet()))))

                                }
                                else -> {
                                    error("Not sure what to do with ${role}.")
                                }
                            }
                        }
                    }
                }
            }
        }
        return Problem(conjectures, axioms, hypotheses, source, detail)
    }
}
