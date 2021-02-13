package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.propositional.AtomicFormula

interface NilOpFormula<F : AtomicFormula> : SimpleSignedFormula<F>
