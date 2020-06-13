package com.benjishults.bitnots.theory.formula

interface CNF : FormulaForm {
    companion object : CNF {
            override val abbreviation: String = "CNF"
    }
}
