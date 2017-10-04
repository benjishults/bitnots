package com.benjishults.bitnots.theory.problem

import com.benjishults.bitnots.theory.formula.AnnotatedFormula

data class Problem(
        val conjectures: List<AnnotatedFormula>,
        val axioms: List<AnnotatedFormula>,
        val hypotheses: List<AnnotatedFormula>,
        val source: ProblemSource = ProblemSource.bitnots,
        val sourceDetail: String = "")

enum class ProblemSource {
    TPTP,
    bitnots
}
