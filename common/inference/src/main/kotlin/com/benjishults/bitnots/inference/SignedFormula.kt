package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.Theory
import com.benjishults.bitnots.util.BranchClosureAttempt

// NOTE these should be immutable
interface SignedFormula<out F : Formula> : BranchClosureAttempt {
    val formula: F
    val sign: Boolean
    fun generateChildren(theory: Theory = Theory): List<SignedFormula<Formula>>

    fun toLocalizedString() = (if (sign) "Suppose: " else "Show: ") + formula

    override fun toString(): String

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}

