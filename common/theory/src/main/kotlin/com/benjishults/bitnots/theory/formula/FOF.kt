package com.benjishults.bitnots.theory.formula

interface FOF : FormulaForm {
    companion object : FOF {
        override val abbreviation: String = "FOF"
    }
}
