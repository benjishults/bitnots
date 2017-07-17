package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.inference.SignedFormula

abstract class ClosingFormula<F : Formula>(formula: F, sign: Boolean) : SimpleSignedFormula<F>(formula, sign)
