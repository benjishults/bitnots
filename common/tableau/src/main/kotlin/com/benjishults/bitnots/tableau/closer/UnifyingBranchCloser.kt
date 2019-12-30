package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.unifier.Substitution

data class UnifyingBranchCloser(val pos: SignedFormula<*>? = null, val neg: SignedFormula<*>? = null, val sub: Substitution) : BranchCloser
