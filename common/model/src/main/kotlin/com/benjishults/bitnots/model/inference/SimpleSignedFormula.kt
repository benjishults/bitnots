package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula

abstract class SimpleSignedFormula<F : Formula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign)