package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula

abstract class NilOpFormula<F : Formula>(formula: F, sign: Boolean) : SimpleSignedFormula<F>(formula, sign)
