package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.Formula

interface NegativeSignedFormula<out F : Formula> :
    SignedFormula<F> {
    override val sign: Boolean
        get() = false
}
