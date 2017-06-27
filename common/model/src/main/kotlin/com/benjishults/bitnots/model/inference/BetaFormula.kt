package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.inference.SignedFormula

abstract class BetaFormula<F : Formula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign)