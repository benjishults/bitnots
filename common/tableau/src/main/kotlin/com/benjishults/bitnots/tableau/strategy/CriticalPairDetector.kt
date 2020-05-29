package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.AllOrNothing
import com.benjishults.bitnots.util.BranchClosureAttempt

interface CriticalPairDetector<out S: BranchClosureAttempt> {

    fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ): S

}

object FolCriticalPairDetector : CriticalPairDetector<Substitution> {
    override fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ) =
        Formula.unify(positiveSignedFormula.formula, negativeSignedFormula.formula, EmptySub)
}

object PropositionalCriticalPairDetector : CriticalPairDetector<AllOrNothing> {
    override fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ) =
        AllOrNothing.of(positiveSignedFormula.formula == negativeSignedFormula.formula)
}
