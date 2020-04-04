package com.benjishults.bitnots.theory

class Theory(val axioms: List<Axiom>) {
    /**
     * the theories known terms
     */
    val termConstructorTable: Map<String, TermConstructorInfo> = mutableMapOf()
    /**
     * the theories known formulas
     */
    val formConstructorTable: Map<String, FormulaConstructorInfo> = mutableMapOf()

}

interface FormulaConstructorInfo {
    val arity : Long
}

interface TermConstructorInfo {
    val arity : Long
}
