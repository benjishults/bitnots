package com.benjishults.bitnots.theory.formula

interface FormulaForm {
    val abbreviation: String
}

object CNF: FormulaForm {
    override val abbreviation: String = "CNF"
}

object FOF : FormulaForm {
    override val abbreviation: String = "FOF"
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

