package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.inference.rules.SignedFormula

abstract class ClosingFormula<F : Formula>(formula: F, sign: Boolean) : SimpleSignedFormula<F>(formula, sign)
