package com.benjishults.bitnots.dataAccess.dao.impl

import com.benjishults.bitnots.dataAccess.dao.ProblemDao
import com.benjishults.bitnots.dataAccess.data.Problem
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.sexpParser.SexpParser
import com.benjishults.bitnots.sexpParser.SexpTokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles

class ProblemDaoImpl(url: String) : BaseDaoImpl<Problem>(url), ProblemDao {
    override fun insertProblem(problem: Problem): Long {
        TODO()
    }

    override fun insertAnnotatedFormula(annotatedFormula: FolAnnotatedFormula): Long =
            getConnection().use { connection ->
                connection.prepareStatement("""
 insert into formula
 (formula, formula_hash) values (?, md5(?))
 on conflict (formula_hash)
 do update set
 formula = ?
 returning formula_id
"""
                ).use {
                    annotatedFormula.formula.toString().let { string ->
                        it.setString(1, string)
                        it.setString(2, string)
                        it.setString(3, string)
                    }
                    it.executeQuery().use {
                        it.next()
                        it.getLong("formula_id")
                    }.let { id ->
                        connection.prepareStatement("""
 insert into formula_plus
 (formula_name, formula_role, formula_id) values
 (?, ?, ?)
 on conflict (formula_name)
 do update
 set formula_name = (select case when formula_plus.formula_id <> EXCLUDED.formula_id then cast(gen_random_uuid() as text) else formula_plus.formula_name end)
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


    override fun getByName(name: String): Problem {
        val conjectures = mutableListOf<AnnotatedFormula>()
        val axioms = mutableListOf<AnnotatedFormula>()
        val hypotheses = mutableListOf<AnnotatedFormula>()
        getConnection().use {
            it.prepareStatement("""
select fp.formula_role, fp.formula_name, f.formula
from problem p
natural join problem_axiom_set
natural join axiom_set
natural join axiom_axiom_set
natural join formula_plus fp
natural join formula f
where p.problem_name = ?
"""
            ).use {
                it.setString(1, name)
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
select 
from problem p
natural join problem_formula_plus pf 
natural join formula_plus f 
where p.problem_name = ?
"""
            ).use {
                it.setString(1, name)
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
        return Problem(name, conjectures, axioms, hypotheses)
    }
}
