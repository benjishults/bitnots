package com.benjishults.bitnots.engine.proof.closer

import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.unifier.Substitution

interface BranchCloser

data class PropositionalBranchCloser(val pos: SignedFormula<*>? = null, val neg: SignedFormula<*>? = null) : BranchCloser

data class UnifyingBranchCloser(val pos: SignedFormula<*>? = null, val neg: SignedFormula<*>? = null, val sub: Substitution) : BranchCloser
