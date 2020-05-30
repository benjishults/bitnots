package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.util.intern.InternTableWithOther

interface Theory {
    val axioms: List<Axiom>

    /**
     * the theories known formulas
     */
    val predicateConstructorTable: InternTableWithOther<Predicate.PredicateConstructor, Int>

    /**
     * the theories known terms
     */
    val functionConstructorTable: InternTableWithOther<Function.FunctionConstructor, Int>

    companion object : Theory {
        override val axioms: List<Axiom> =
            emptyList()
        override val predicateConstructorTable: InternTableWithOther<Predicate.PredicateConstructor, Int> =
            Predicate.PredicateConstructor
        override val functionConstructorTable: InternTableWithOther<Function.FunctionConstructor, Int> =
            Function.FunctionConstructor
    }

}
