package com.benjishults.bitnots.theory.language

import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.terms.Function

interface Language {
}

class FolLanguage(predicates: List<Predicate>, functions: List<Function>) : Language {
    
}

class PropositionalLanguage(propositions: List<PropositionalVariable> = emptyList()) : Language {

    val propositions: MutableList<PropositionalVariable> = propositions.toMutableList()

    override fun toString(): String = buildString {
        append("[")
                .append(propositions.map { it.constructor.name }.joinToString())
                .append("]")
    }

}

