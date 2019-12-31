package com.benjishults.bitnots.engine

import com.benjishults.bitnots.engine.prover.ProofConstraints
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
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
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.test.Claim
import com.benjishults.bitnots.test.FalseClaim
import com.benjishults.bitnots.test.TrueClaim
import org.junit.Test

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
                TrueClaim(Implies(Tfae(A, B, C), Implies(B, A)), steps = ProofConstraints(1)),
                TrueClaim(Tfae(P, P, P), steps = ProofConstraints(2)),
                TrueClaim(Implies(And(A, B), Iff(A, B)), steps = ProofConstraints(1)),
                TrueClaim(Or(Iff(A, B), A, B), steps = ProofConstraints(1)),
                TrueClaim(Implies(Implies(A, B), Implies(A, B)), steps = ProofConstraints(1)),
                TrueClaim(Implies(Falsity, P), steps = ProofConstraints(0)),
                TrueClaim(Implies(P, P), steps = ProofConstraints(0)),
                TrueClaim(Or(P, Not(P)), steps = ProofConstraints(0)),
                TrueClaim(And(Truth, Truth), steps = ProofConstraints(1)),
                TrueClaim(Implies(Falsity, Falsity), steps = ProofConstraints(0)),
                TrueClaim(Implies(Falsity, Truth), steps = ProofConstraints(0)),
                TrueClaim(Implies(Truth, Truth), steps = ProofConstraints(0)),
                TrueClaim(Truth, steps = ProofConstraints(0)),
                TrueClaim(Implies(Implies(Truth, Falsity), Falsity), steps = ProofConstraints(1)),
                FalseClaim(Tfae(A, B, C)),
                FalseClaim(Implies(Or(R, P), Or(And(P, Q), R))),
                FalseClaim(Implies(And(
                        Implies(And(A, B), C),
                        Implies(Truth, A)), C)),
                FalseClaim(Implies(
                        And(
                                Implies(And(A, B), C),
                                Implies(And(A1, A2), A),
                                Implies(And(A11, A12), A1),
                                Implies(Truth, A11),
                                Implies(Truth, A12),
                                Implies(Truth, A2),
                                Implies(And(B1, B2), B),
                                Implies(Truth, B1),
                                Implies(And(E, F), C)),
                        C)),
                FalseClaim(Implies(A, Or(C, B))),
                FalseClaim(Implies(Or(A, B), Or(C, B))),
                FalseClaim(Implies(Or(A, B), Or(C, B))),
                FalseClaim(Implies(Or(A, B), Implies(A, B))),
                FalseClaim(And(Truth, Falsity)),
                FalseClaim(Falsity)
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
                TrueClaim(Implies(
                        And(
                                ForAll(x,
                                       formula = Implies(P_(x), Q_(x))),
                                ForSome(y,
                                        formula = Implies(Q_(y), R_(y)))),
                        ForSome(z,
                                formula = Implies(P_(z), R_(z)))
                ),
                          steps = ProofConstraints(5, 12)),
                /*
                TrueClaim(ForAll(a,
                        formula = ForSome(x, x2, x3, x4, y,
                                formula = Implies(
                                        And(
                                                P_(a),
                                                E_(a),
                                                Implies(E_(x),
                                                        Or(G(x), S(x, f(x)))),
                                                Implies(E_(x2),
                                                        Or(G(x2), C_(f(x2)))),
                                                Implies(S(a, y), P_(y))),
                                        Or(
                                                And(P_(x3), G(x3)),
                                                And(P_(x4), C_(x4)))))),
                        steps = Count(100)),
                 */
                FalseClaim(Implies(And(
                        ForSome(x,
                                formula = P_(x)),
                        ForSome(x,
                                formula = Q_(x))
                ), ForSome(y, formula = And(P_(y), Q_(y))))
                ),
                FalseClaim(Implies(
                        ForSome(x,
                                formula = P_(x)),
                        ForAll(x,
                               formula = P_(x))))

        )
        // this would require some set theory implementation
        //        val isContinuous = Pred("isContinuous", 3)
        //        val projection = Fn("projection", 3)
        //        val product = Fn("product", 2)
        //        val apply = Fn("apply", 2)
        //        val isOpenOnto = Pred("isOpenOnto", 3)
        //        val locallyCompact = Pred("locallyCompact", 1)
        //        val isFinite = Pred("isFinite", 1)
        //        val member = Pred("member", 2)
        //        val compact = Pred("compact", 1)
        //        val X = BV("X")
        //        val A_ = BV("A")
        //        val a_ = BV("a")
        //
        //
        //        val LOCALLY_COMPACT = arrayOf(
        //                Claim(Implies(And()), Implies(locallyCompact(product(X, A)))
        //        )
    }

    @Test
    fun testProps() {
        testClaims(PROP_CLAIMS, { n: PropositionalTableauNode ->
            PropositionalTableau(n)
        }) { forms, p ->
            PropositionalTableauNode(forms, p)
        }
    }

    @Test
    //    @Ignore
    fun testFols() {
        testClaims<FolTableauNode, FolTableau>(FOL_CLAIMS, { n: FolTableauNode ->
            FolTableau(n)
        }) { forms, p: FolTableauNode? ->
            FolTableauNode(forms, p)
        }
    }

    private fun <N : TableauNode, T : Tableau> testClaims(
            claims: Array<Claim>,
            tabFactory: (N) -> T,
            nodeFactory: (MutableList<SignedFormula<Formula<*>>>, N?) -> N
    ) {
        for (claim in claims) {
            claim.validate(
                    tabFactory(nodeFactory(ArrayList<SignedFormula<Formula<*>>>().also {
                        it.add(claim.formula.createSignedFormula())
                    }, null)))
        }
    }
}
