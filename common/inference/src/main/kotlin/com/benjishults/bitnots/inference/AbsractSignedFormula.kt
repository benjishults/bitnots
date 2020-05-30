package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.Formula

abstract class AbsractSignedFormula<out F : Formula> :
    SignedFormula<F> {
    override fun toString() = (if (sign) "Suppose: " else "Show: ") + formula

    override fun equals(other: Any?): Boolean {
        return other is SignedFormula<*> && other.sign == sign && other.formula == formula
    }
}
