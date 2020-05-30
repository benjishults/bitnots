package com.benjishults.bitnots.engine

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.formulas.propositional.and
import com.benjishults.bitnots.model.formulas.propositional.iff
import com.benjishults.bitnots.model.formulas.propositional.implies
import com.benjishults.bitnots.model.formulas.propositional.or
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.test.Claim
import com.benjishults.bitnots.test.FalseFolClaim
import com.benjishults.bitnots.test.FalsePropClaim
import com.benjishults.bitnots.test.TrueFolClaim
import com.benjishults.bitnots.test.TruePropClaim
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TableauStepCountTest {

    companion object {
        val A = PropositionalVariable.intern("A")
        val A1 = PropositionalVariable.intern("A1")
        val A2 = PropositionalVariable.intern("A2")
        val A11 = PropositionalVariable.intern("A11")
        val A12 = PropositionalVariable.intern("A12")
        val B = PropositionalVariable.intern("B")
        val B1 = PropositionalVariable.intern("B1")
        val B2 = PropositionalVariable.intern("B2")
        val C = PropositionalVariable.intern("C")
        val E = PropositionalVariable.intern("E")
        val F = PropositionalVariable.intern("F")
        val P = PropositionalVariable.intern("P")
        val Q = PropositionalVariable.intern("Q")
        val R = PropositionalVariable.intern("R")

        val PROP_CLAIMS = arrayOf(
            TruePropClaim(
                Tfae(A, B, C) implies (B implies A),
                maxSteps = 1
            ),
            TruePropClaim(Tfae(P, P, P), maxSteps = 2),
            TruePropClaim(
                (A and B) implies (A iff B),
                maxSteps = 1
            ),
            TruePropClaim(
                (A iff B) or A or B,
                maxSteps = 1
            ),
            TruePropClaim(
                (A implies B) implies (A implies B),
                maxSteps = 1
            ),
            TruePropClaim(
                Falsity implies P,
                maxSteps = 0
            ),
            TruePropClaim(
                P implies P,
                maxSteps = 0
            ),
            TruePropClaim(
                P or Not(P),
                maxSteps = 0
            ),
            TruePropClaim(
                Truth and Truth,
                maxSteps = 1
            ),
            TruePropClaim(
                Falsity implies Falsity,
                maxSteps = 0
            ),
            TruePropClaim(
                Falsity implies Truth,
                maxSteps = 0
            ),
            TruePropClaim(
                Truth implies Truth,
                maxSteps = 0
            ),
            TruePropClaim(Truth, maxSteps = 0),
            TruePropClaim(
                (Truth implies Falsity) implies Falsity,
                maxSteps = 1
            ),
            FalsePropClaim(Tfae(A, B, C)),
            FalsePropClaim(
                (R or P) implies ((P and Q) or R)
            ),
            FalsePropClaim(
                (((A and B) implies C) and (Truth implies A) implies C)
            ),
            FalsePropClaim(
                And(
                    (A and B) implies C,
                    (A1 and A2) implies A,
                    (A11 and A12) implies A1,
                    Truth implies A11,
                    Truth implies A12,
                    Truth implies A2,
                    (B1 and B2) implies B,
                    Truth implies B1,
                    (E and F) implies C
                ) implies
                        C
            ),
            FalsePropClaim(
                A implies (C or B)
            ),
            FalsePropClaim(
                (A or B) implies (C or B)
            ),
            FalsePropClaim(
                (A or B) implies (C or B)
            ),
            FalsePropClaim(
                (A or B) implies (A implies B)
            ),
            FalsePropClaim(
                Truth and Falsity
            ),
            FalsePropClaim(Falsity)
        )

        val a = BoundVariable.intern("a")
        val x4 = BoundVariable.intern("x4")
        val x3 = BoundVariable.intern("x3")
        val y = BoundVariable.intern("y")
        val z = BoundVariable.intern("z")
        val x2 = BoundVariable.intern("x2")
        val x = BoundVariable.intern("x")

        val f = Fn("f")

        val C_ = Pred("C", 1)
        val E_ = Pred("E", 1)
        val G = Pred("G", 1)
        val R_ = Pred("R", 1)
        val S = Pred("S", 2)
        val P_ = Pred("P", 1)
        val Q_ = Pred("Q", 1)

        val FOL_CLAIMS = arrayOf(
            TrueFolClaim(
                (
                        ForAll(x, formula = Implies(P_(x), Q_(x))) and
                                ForSome(y, formula = Implies(Q_(y), R_(y)))
                        ) implies
                        ForSome(z, formula = Implies(P_(z), R_(z)))
                ,
                minSteps = 5,
                maxSteps = 12
            ),
            TrueFolClaim(
                ForAll(
                    a,
                    formula = ForSome(
                        x, x2, x3, x4, y,
                        formula =
                        And(
                            P_(a),
                            E_(a),
                            E_(x) implies (G(x) or S(x, f(x))),
                            E_(x2) implies (G(x2) or C_(f(x2))),
                            S(a, y) implies P_(y)
                        ) implies (
                                (P_(x3) and G(x3))
                                        or (P_(x4) and C_(x4)))
                    )
                ),
                minSteps = 1,
                maxSteps = 12
            ),

            FalseFolClaim(

                (ForSome(x, formula = P_(x)) and
                        ForSome(x, formula = Q_(x)))
                        implies
                        ForSome(y, formula = And(P_(y), Q_(y)))
                ,
                qLimit = 3
            ),
            FalseFolClaim(
                ForSome(x, formula = P_(x)) implies
                        ForAll(x, formula = P_(x))
            )
        )
    }

    @Test
    fun testProps() =
        testClaims(PROP_CLAIMS)


    @Test
    fun testFols() =
        testClaims(FOL_CLAIMS)

    private fun <C : Claim<*, *>> testClaims(claims: Array<C>) = runBlocking {
        claims.forEach {
            it.validate(it.attempt()) || error("failed: $it")
        }
    }

}
