package com.benjishults.bitnots.dataAccess.data

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.formula.AnnotatedFormula

data class Problem(val name: String, val conjectures: List<AnnotatedFormula>, val axioms : List<AnnotatedFormula>, val hypotheses: List<AnnotatedFormula>)
