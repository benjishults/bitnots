package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.util.intern.InternTable

open class FormulaConstructor(val name: String) {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return it::class === this::class && (it as FormulaConstructor).name == name
        } ?: return false
    }

    override fun hashCode() = name.hashCode()

    // used by logical constructs
    companion object : InternTable<FormulaConstructor>({ name -> FormulaConstructor(name) })

}

open class PropositionalFormulaConstructor(name: String): FormulaConstructor(name) {
    // used by logical constructs
    companion object : InternTable<PropositionalFormulaConstructor>({ name -> PropositionalFormulaConstructor(name) })

}
