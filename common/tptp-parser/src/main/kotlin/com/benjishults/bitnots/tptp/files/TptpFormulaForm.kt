package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.theory.formula.CNF
import com.benjishults.bitnots.theory.formula.FOF
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.theory.formula.TFF
import com.benjishults.bitnots.theory.formula.TFFWithArithmetic
import com.benjishults.bitnots.theory.formula.THF

sealed class TptpFormulaForm(val representation: Char) : FormulaForm {

    init {
        TptpFormulaForm.map[representation] = this
    }

    companion object {
        private val map = mutableMapOf<Char, TptpFormulaForm>()
        val abbreviationToInstanceMap = mapOf(
            CNF.abbreviation to TptpCnf,
            FOF.abbreviation to TptpFof
        )

        // fun values() = map.values

        fun findByRepresentation(representation: Char) =
            map.getOrElse(representation) { throw IllegalArgumentException("Unknown representation: '$representation'") }
    }

    override fun toString(): String = this::class.simpleName!!

}

object TptpCnf : TptpFormulaForm('-'), CNF by CNF

object TptpFof : TptpFormulaForm('+'), FOF by FOF

object TptpTff : TptpFormulaForm('_'), FormulaForm by TFF

object TptpTffWithArithmetic : TptpFormulaForm('='), FormulaForm by TFFWithArithmetic

object TptpThf : TptpFormulaForm('^'), FormulaForm by THF
