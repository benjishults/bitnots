package com.benjishults.bitnots.model.formulas.fol.equality

import com.benjishults.bitnots.model.formulas.fol.Predicate.PredicateConstructor

// TODO I want this loaded only if we are working in a theory of equlity
object Equals : PredicateConstructor("=", 2) {
}
        