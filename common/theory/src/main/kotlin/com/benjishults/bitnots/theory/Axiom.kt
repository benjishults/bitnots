package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.Formula

data class Axiom(val formula: Formula<*>, val name: String = "") {
}