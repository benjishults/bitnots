/*package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term

data class TptpFormulaWrapper1(val formula: Formula<*>, val type: TptpProduction1)

sealed class TptpProduction1

open class Binary1 : TptpProduction1()
open class Unitary1 : TptpProduction1()

object Assoc1: Binary1()
object NonAssoc1 : Binary1()
object Unary1 : Unitary1()
object Paren1 : Unitary1()
object Quantified1 : Unitary1()
object Atomic1 : Unitary1()

sealed class BinaryConnector1(open val connector: String) {

    abstract class NonAssociativeBinaryConnector(override val connector: String) : BinaryConnector1(connector)
    abstract class AssociativeBinaryConnector(override val connector: String) : BinaryConnector1(connector)

    object NoConnector : BinaryConnector1(" ")

    object AndConnector : AssociativeBinaryConnector("&")
    object OrConnector : AssociativeBinaryConnector("|")

    object IffConnector : NonAssociativeBinaryConnector("<=>")
    object ImpliesConnector : NonAssociativeBinaryConnector("=>")
    object ReverseImpliesConnector : NonAssociativeBinaryConnector("<=")
    object XorConnector : NonAssociativeBinaryConnector("<->")
    object NorConnector : NonAssociativeBinaryConnector("~|")
    object NandConnector : NonAssociativeBinaryConnector("~&")

    companion object {
        fun values() = BinaryConnector::class.nestedClasses.filter { it.isFinal && !it.isCompanion }.map { it.objectInstance as BinaryConnector }.toList()
        fun forString(connector: String) = values().firstOrNull { it.connector == connector }
    }

}

*/