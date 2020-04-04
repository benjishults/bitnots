package com.benjishults.bitnots.tptp.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.parser.formula.FormulaClassifier
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole

class TptpFormulaClassifier: FormulaClassifier {
    override fun classify(formulas: List<AnnotatedFormula>): Pair<MutableList<Formula>, MutableList<Formula>> {
        return formulas.fold(
            mutableListOf<Formula>() to mutableListOf()
        ) { (hyps, targets), input ->
            input.let { annotated ->
                when (annotated.formulaRole) {
                    FormulaRole.axiom,
                    FormulaRole.hypothesis,
                    FormulaRole.assumption,
                    FormulaRole.definition,
                    FormulaRole.theorem,
                    FormulaRole.lemma              -> {
                        hyps.add(annotated.formula)
                    }

                    FormulaRole.conjecture         -> {
                        targets.add(annotated.formula)
                    }
                    FormulaRole.negated_conjecture -> {
                        targets.add(Not(annotated.formula))
                    }

                    FormulaRole.corollary,
                    FormulaRole.fi_domain,
                    FormulaRole.fi_functors,
                    FormulaRole.fi_predicates,
                    FormulaRole.plain,
                    FormulaRole.type               -> {
                        error("Don't know what to do with ${annotated.formulaRole}.")
                    }
                    FormulaRole.unknown            -> {
                        error("Unknown role found.")
                    }
                }
            }
            hyps to targets
        }
    }
}
