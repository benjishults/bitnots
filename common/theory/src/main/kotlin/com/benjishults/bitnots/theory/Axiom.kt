package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.Formula
import kotlinx.serialization.Serializable

@Serializable
data class Axiom(val formula: Formula, val name: String = "") {
}
