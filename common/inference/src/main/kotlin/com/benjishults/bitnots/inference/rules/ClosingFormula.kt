package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula

interface ClosingFormula<F : Formula> : SimpleSignedFormula<F>
