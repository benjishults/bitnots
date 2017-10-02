package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.formula.SimpleSignedFormula

abstract class NilOpFormula<F : Formula<*>>(formula: F, sign: Boolean) : SimpleSignedFormula<F>(formula, sign)
