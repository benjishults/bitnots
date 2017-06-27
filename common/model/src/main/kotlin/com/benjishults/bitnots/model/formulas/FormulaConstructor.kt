package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.util.InternTable

open class FormulaConstructor private constructor (val name: String) {
	companion object inner : InternTable<FormulaConstructor, Nothing>({ name, _ -> FormulaConstructor(name) })
}