package com.benjishults.bitnots.theory.language

import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.formulas.propositional.Prop

interface Language {
}

class PropositionalLanguage(propositions: List<PropositionalVariable> = emptyList()) : Language {

    val propositions: MutableList<PropositionalVariable> = propositions.toMutableList()

    fun addAll(names: Iterable<String>) {
        // TODO what to do about name changes?  I need to throw an exception here.
        propositions.addAll(names.map {
            Prop(it)
        })
    }

    override fun toString(): String = buildString {
        append("[")
                .append(propositions.map { it.constructor.name }.joinToString())
                .append("]")
    }

}

