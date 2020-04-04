package com.benjishults.bitnots.theory

import com.benjishults.bitnots.theory.formula.FormulaForm

interface ProblemDescriptor {
    val domain: DomainCategory
    val form: FormulaForm
    val number: Int
    val version: Int
    val size: Int
}
