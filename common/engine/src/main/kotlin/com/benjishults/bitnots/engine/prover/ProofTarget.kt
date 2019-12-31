package com.benjishults.bitnots.engine.prover

interface ProofTarget {

    fun prover()

    //    fun prove(formula: Formula<*>) {
    //        val tableau = Tableau(TableauNode(ArrayList<SignedFormula<Formula<*>>>().also {
    //            it.add(formula.createSignedFormula())
    //        }, null))
    //        println(tableau)
    //        println("is ${if (!tableau.isClosed()) "not" else ""} closed")
    //    }
}

interface Prover {

    fun prove(proofTarget: ProofTarget)

}

