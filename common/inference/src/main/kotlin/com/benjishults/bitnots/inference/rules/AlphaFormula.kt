package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

abstract class AlphaFormula<F : Formula<*>>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign)
