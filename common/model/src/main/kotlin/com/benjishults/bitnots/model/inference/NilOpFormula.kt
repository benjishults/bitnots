package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula

abstract class NilOpFormula<F : Formula>(formula: F, sign: Boolean) : SimpleSignedFormula<F>(formula, sign)
