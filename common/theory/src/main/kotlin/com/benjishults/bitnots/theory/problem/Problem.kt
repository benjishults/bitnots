package com.benjishults.bitnots.theory.problem

import com.benjishults.bitnots.theory.formula.AnnotatedFormula

data class Problem (
    val hypotheses: List<AnnotatedFormula>,
    val conjectures: List<AnnotatedFormula>
)
