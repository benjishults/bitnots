package com.benjishults.bitnots.engine

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
import com.benjishults.bitnots.test.Claim
import com.benjishults.bitnots.test.FalseFolClaim
import com.benjishults.bitnots.test.FalsePropClaim
import com.benjishults.bitnots.test.TrueFolClaim
import com.benjishults.bitnots.test.TruePropClaim
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TableauStepCountTest {

    companion object {
        val A = Prop("A")
        val A1 = Prop("A1")
        val A2 = Prop("A2")
        val A11 = Prop("A11")
        val A12 = Prop("A12")
        val B = Prop("B")
        val B1 = Prop("B1")
        val B2 = Prop("B2")
        val C = Prop("C")
        val E = Prop("E")
        val F = Prop("F")
        val P = Prop("P")
        val Q = Prop("Q")
        val R = Prop("R")

        val PROP_CLAIMS = arrayOf(
            TruePropClaim(Implies(Tfae(A, B, C), Implies(B, A)), maxSteps = 1),
            TruePropClaim(Tfae(P, P, P), maxSteps = 2),
            TruePropClaim(Implies(And(A, B), Iff(A, B)), maxSteps = 1),
            TruePropClaim(Or(Iff(A, B), A, B), maxSteps = 1),
            TruePropClaim(Implies(Implies(A, B), Implies(A, B)), maxSteps = 1),
            TruePropClaim(Implies(Falsity, P), maxSteps = 0),
            TruePropClaim(Implies(P, P), maxSteps = 0),
            TruePropClaim(Or(P, Not(P)), maxSteps = 0),
            TruePropClaim(And(Truth, Truth), maxSteps = 1),
            TruePropClaim(Implies(Falsity, Falsity), maxSteps = 0),
            TruePropClaim(Implies(Falsity, Truth), maxSteps = 0),
            TruePropClaim(Implies(Truth, Truth), maxSteps = 0),
            TruePropClaim(Truth, maxSteps = 0),
            TruePropClaim(Implies(Implies(Truth, Falsity), Falsity), maxSteps = 1),
            FalsePropClaim(Tfae(A, B, C)),
            FalsePropClaim(Implies(Or(R, P), Or(And(P, Q), R))),
            FalsePropClaim(
                Implies(
                    And(
                        Implies(And(A, B), C),
                        Implies(Truth, A)
                    ), C
                )
            ),
            FalsePropClaim(
                Implies(
                    And(
                        Implies(And(A, B), C),
                        Implies(And(A1, A2), A),
                        Implies(And(A11, A12), A1),
                        Implies(Truth, A11),
                        Implies(Truth, A12),
                        Implies(Truth, A2),
                        Implies(And(B1, B2), B),
                        Implies(Truth, B1),
                        Implies(And(E, F), C)
                    ),
                    C
                )
            ),
            FalsePropClaim(Implies(A, Or(C, B))),
            FalsePropClaim(Implies(Or(A, B), Or(C, B))),
            FalsePropClaim(Implies(Or(A, B), Or(C, B))),
            FalsePropClaim(Implies(Or(A, B), Implies(A, B))),
            FalsePropClaim(And(Truth, Falsity)),
            FalsePropClaim(Falsity)
        )

        val a = BV("a")
        val x4 = BV("x4")
        val x3 = BV("x3")
        val y = BV("y")
        val z = BV("z")
        val x2 = BV("x2")
        val x = BV("x")

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
                Implies(
                    And(
                        ForAll(x, formula = Implies(P_(x), Q_(x))),
                        ForSome(y, formula = Implies(Q_(y), R_(y)))
                    ),
                    ForSome(z, formula = Implies(P_(z), R_(z)))
                ),
                minSteps = 5,
                maxSteps = 12
            ),
            TrueFolClaim(
                ForAll(
                    a,
                    formula = ForSome(
                        x, x2, x3, x4, y,
                        formula = Implies(
                            And(
                                P_(a),
                                E_(a),
                                Implies(
                                    E_(x),
                                    Or(G(x), S(x, f(x)))
                                ),
                                Implies(
                                    E_(x2),
                                    Or(G(x2), C_(f(x2)))
                                ),
                                Implies(S(a, y), P_(y))
                            ),
                            Or(
                                And(P_(x3), G(x3)),
                                And(P_(x4), C_(x4))
                            )
                        )
                    )
                ),
                minSteps = 1,
                maxSteps = 12
            ),

            FalseFolClaim(
                Implies(
                    And(
                        ForSome(x, formula = P_(x)),
                        ForSome(x, formula = Q_(x))
                    ),
                    ForSome(y, formula = And(P_(y), Q_(y)))
                )
            ),
            FalseFolClaim(
                Implies(
                    ForSome(x, formula = P_(x)),
                    ForAll(x, formula = P_(x))
                )
            )

        )
    }

    @Test
    fun testProps() = testClaims(PROP_CLAIMS)


    @Test
    fun testFols() =
        testClaims(FOL_CLAIMS)

    private fun <C : Claim<*>> testClaims(claims: Array<C>) = runBlocking {
        claims.forEach {
            it.validate(it.attempt()) || error("failed: $it")
        }
    }

}
