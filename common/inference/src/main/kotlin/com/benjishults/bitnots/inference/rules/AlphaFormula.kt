package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

interface AlphaFormula<F : Formula> : SignedFormula<F>
