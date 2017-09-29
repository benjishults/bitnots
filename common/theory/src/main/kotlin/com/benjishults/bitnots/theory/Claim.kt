package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.Fn

val DEFAULT_MAX_STEPS: Int = 30

data class ClosedInterval(val min: Int, val max: Int = min)

sealed class Claim(
        val formula: Formula<*>
)

class FalseClaim(
        formula: Formula<*>
) : Claim(formula)

class TrueClaim(
        formula: Formula<*>,
        val steps: ClosedInterval = ClosedInterval(0, DEFAULT_MAX_STEPS)
) : Claim(formula)
