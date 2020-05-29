package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.util.Match

interface ClosingFormula<F : Formula> : SimpleSignedFormula<F>, Match
