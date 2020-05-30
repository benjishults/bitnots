package com.benjishults.bitnots.inference

import com.benjishults.bitnots.inference.rules.concrete.NegativeAnd
import com.benjishults.bitnots.inference.rules.concrete.NegativeFalsity
import com.benjishults.bitnots.inference.rules.concrete.NegativeForAll
import com.benjishults.bitnots.inference.rules.concrete.NegativeForSome
import com.benjishults.bitnots.inference.rules.concrete.NegativeIff
import com.benjishults.bitnots.inference.rules.concrete.NegativeImplies
import com.benjishults.bitnots.inference.rules.concrete.NegativeNot
import com.benjishults.bitnots.inference.rules.concrete.NegativeOr
import com.benjishults.bitnots.inference.rules.concrete.NegativePredicate
import com.benjishults.bitnots.inference.rules.concrete.NegativePropositionalVariable
import com.benjishults.bitnots.inference.rules.concrete.NegativeTfae
import com.benjishults.bitnots.inference.rules.concrete.NegativeTruth
import com.benjishults.bitnots.inference.rules.concrete.PositiveAnd
import com.benjishults.bitnots.inference.rules.concrete.PositiveFalsity
import com.benjishults.bitnots.inference.rules.concrete.PositiveForAll
import com.benjishults.bitnots.inference.rules.concrete.PositiveForSome
import com.benjishults.bitnots.inference.rules.concrete.PositiveIff
import com.benjishults.bitnots.inference.rules.concrete.PositiveImplies
import com.benjishults.bitnots.inference.rules.concrete.PositiveNot
import com.benjishults.bitnots.inference.rules.concrete.PositiveOr
import com.benjishults.bitnots.inference.rules.concrete.PositivePredicate
import com.benjishults.bitnots.inference.rules.concrete.PositivePropositionalVariable
import com.benjishults.bitnots.inference.rules.concrete.PositiveTfae
import com.benjishults.bitnots.inference.rules.concrete.PositiveTruth
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.theory.Theory
import com.benjishults.bitnots.util.BranchClosureAttempt
import kotlin.reflect.KClass

// NOTE these should be immutable
interface SignedFormula<out F : Formula> : BranchClosureAttempt {
    val formula: F
    val sign: Boolean
    fun generateChildren(theory: Theory = Theory): List<SignedFormula<Formula>>

    fun toLocalizedString() = (if (sign) "Suppose: " else "Show: ") + formula

    override fun toString(): String

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}

abstract class AbsractSignedFormula<out F : Formula> : SignedFormula<F> {
    override fun toString() = (if (sign) "Suppose: " else "Show: ") + formula

    override fun equals(other: Any?): Boolean {
        return other is SignedFormula<*> && other.sign == sign && other.formula == formula
    }
}

interface NegativeSignedFormula<out F : Formula> : SignedFormula<F> {
    override val sign: Boolean
        get() = false
}

interface PositiveSignedFormula<out F : Formula> : SignedFormula<F> {
    override val sign: Boolean
        get() = true
}

object SignedFormulaFactory {

    val classToSignedFormulaFactory = mutableMapOf<KClass<*>, (Formula, Boolean) -> SignedFormula<*>>()

    init {
        classToSignedFormulaFactory[ForSome::class] = { formula, sign ->
            if (sign) {
                PositiveForSome(formula as ForSome)
            } else {
                NegativeForSome(formula as ForSome)
            }
        }
        classToSignedFormulaFactory[ForAll::class] = { formula, sign ->
            if (sign) {
                PositiveForAll(formula as ForAll)
            } else {
                NegativeForAll(formula as ForAll)
            }
        }
        classToSignedFormulaFactory[Not::class] = { formula, sign ->
            if (sign) {
                PositiveNot(formula as Not)
            } else {
                NegativeNot(formula as Not)
            }
        }
        classToSignedFormulaFactory[Tfae::class] = { formula, sign ->
            if (sign) {
                PositiveTfae(formula as Tfae)
            } else {
                NegativeTfae(formula as Tfae)
            }
        }
        classToSignedFormulaFactory[Iff::class] = { formula, sign ->
            if (sign) {
                PositiveIff(formula as Iff)
            } else {
                NegativeIff(formula as Iff)
            }
        }
        classToSignedFormulaFactory[Truth::class] = { formula, sign ->
            if (sign) {
                PositiveTruth
            } else {
                NegativeTruth
            }
        }
        classToSignedFormulaFactory[Falsity::class] = { formula, sign ->
            if (sign) {
                PositiveFalsity
            } else {
                NegativeFalsity
            }
        }
        classToSignedFormulaFactory[Implies::class] = { formula, sign ->
            if (sign) {
                PositiveImplies(formula as Implies)
            } else {
                NegativeImplies(formula as Implies)
            }
        }
        classToSignedFormulaFactory[Or::class] = { formula, sign ->
            if (sign) {
                PositiveOr(formula as Or)
            } else {
                NegativeOr(formula as Or)
            }
        }
        classToSignedFormulaFactory[And::class] = { formula, sign ->
            if (sign) {
                PositiveAnd(formula as And)
            } else {
                NegativeAnd(formula as And)
            }
        }
        classToSignedFormulaFactory[Predicate::class] = { formula, sign ->
            if (sign) {
                PositivePredicate(formula as Predicate)
            } else {
                NegativePredicate(formula as Predicate)
            }
        }
        classToSignedFormulaFactory[PropositionalVariable::class] = { formula, sign ->
            if (sign) {
                PositivePropositionalVariable(formula as PropositionalVariable)
            } else {
                NegativePropositionalVariable(formula as PropositionalVariable)
            }
        }
    }

    fun registerBuilder(formulaClass: KClass<Formula>, factory: (Formula, Boolean) -> SignedFormula<*>) {
        classToSignedFormulaFactory[formulaClass] = factory
    }

    /**
     * For each concrete subtype "T" of Formula, there must be subclasses of SignedFormula with names NegativeT and PositiveT.
     * These SignedFormula classes must have a constructor that takes no arguments or one that takes a single Formula argument.
     */
    fun createSignedFormula(formula: Formula,
        sign: Boolean = false
    ): SignedFormula<*> =
        classToSignedFormulaFactory[formula::class]!!(formula, sign)

}
