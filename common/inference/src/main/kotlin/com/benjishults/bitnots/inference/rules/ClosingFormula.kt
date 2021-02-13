package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.propositional.AtomicFormula
import com.benjishults.bitnots.util.Match

interface ClosingFormula<F : AtomicFormula> : SimpleSignedFormula<F>, Match
