package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.inference.SignedFormula

data class PropositionalBranchCloser(val pos: SignedFormula<*>? = null, val neg: SignedFormula<*>? = null) : BranchCloser
