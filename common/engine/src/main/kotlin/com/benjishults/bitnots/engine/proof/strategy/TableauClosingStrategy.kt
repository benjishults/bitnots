package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.closer.PropositionalBranchCloser
import com.benjishults.bitnots.engine.proof.closer.UnifyingBranchCloser
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable

interface TableauClosingStrategy {
    fun populateBranchClosers(tableau: Tableau)
}
