package com.benjishults.bitnots.engine.prover

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode

fun Formula<*>.prove() {
    val tableau = FolTableau(FolTableauNode(mutableListOf(createSignedFormula(false))))
    println(tableau)
    println("is ${if (!tableau.root.isClosed()) "not" else ""} closed")
}


