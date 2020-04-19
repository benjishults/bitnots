package com.benjishults.bitnots.theory.formula

interface FormulaForm {
    val abbreviation: String
}

interface CNF : FormulaForm {
    companion object {
        val IMPL = object : CNF {
            override val abbreviation: String = "CNF"
        }
    }
}

interface FOF : FormulaForm {
    companion object {
        val IMPL = object : FOF {
            override val abbreviation: String = "FOF"
        }
    }
}

/**
 * monomorphic and polymorphic typed first-order form
 */
object TFF : FormulaForm {
    override val abbreviation: String = "TFF"
}

object TFFWithArithmetic : FormulaForm {
    override val abbreviation: String = "TFF+"

}

/**
 * monomorphic and polymorphic typed higher-order form
 */
object THF : FormulaForm {
    override val abbreviation: String = "THF"

}

