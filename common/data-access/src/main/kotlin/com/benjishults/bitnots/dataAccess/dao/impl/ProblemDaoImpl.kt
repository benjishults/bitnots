package com.benjishults.bitnots.dataAccess.dao.impl

import com.benjishults.bitnots.dataAccess.dao.ProblemDao
import com.benjishults.bitnots.dataAccess.data.Problem
import com.benjishults.bitnots.sexpParser.SexpParser
import com.benjishults.bitnots.sexpParser.SexpTokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FofAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles

class ProblemDaoImpl(url: String) : BaseDaoImpl<Problem>(url), ProblemDao {
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
                        axioms.add(FofAnnotatedFormula(
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
                    }
                }
            }
        }
        return Problem(name, conjectures, axioms, hypotheses)
    }
}
