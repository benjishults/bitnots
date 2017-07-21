package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.util.InternTable

open class FormulaConstructor(val name: String) {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return it::class === this::class && (it as FormulaConstructor).name == name
        } ?: return false
    }

    override fun hashCode() = name.hashCode()

    companion object : InternTable<FormulaConstructor>({ name -> FormulaConstructor(name) })

}