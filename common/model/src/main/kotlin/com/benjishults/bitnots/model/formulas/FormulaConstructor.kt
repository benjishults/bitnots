package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.util.InternTable

open class FormulaConstructor private constructor(val name: String) {
	companion object : InternTable<FormulaConstructor>({ name -> FormulaConstructor(name) })
}