package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.formula.SignedFormula

abstract class BetaFormula<out F : Formula<*>>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign)