package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.ForSome
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

val DEFAULT_MAX_STEPS: Int = 10

sealed class CountSC { // : Comparable<CountSC> {
    abstract fun toInt(): Int
}

class Count(val count: Int = DEFAULT_MAX_STEPS) : CountSC() {
    override fun toInt(): Int = count

}

object ZeroCount : CountSC() {
    override fun toInt(): Int = 0
}

object OneCount : CountSC() {
    override fun toInt(): Int = 1
}

object TwoCount : CountSC() {
    override fun toInt(): Int = 2
}

object NoCount : CountSC() {
    override fun toInt(): Int = throw IllegalStateException()
}

data class Claim(
        val formula: Formula<*>,
        val provable: Boolean = true,
        val gamma: CountSC = Count(DEFAULT_MAX_STEPS),
        val steps: CountSC = NoCount // Count(DEFAULT_MAX_STEPS)
) {

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
                Claim(Implies(Tfae(A, B, C), Implies(B, A)), steps = OneCount),
                Claim(Tfae(P, P, P), steps = TwoCount),
                Claim(Implies(And(A, B), Iff(A, B)), steps = OneCount),
                Claim(Or(Iff(A, B), A, B), steps = OneCount),
                Claim(Implies(Implies(A, B), Implies(A, B)), steps = OneCount),
                Claim(Implies(Falsity, P), steps = ZeroCount),
                Claim(Implies(P, P), steps = ZeroCount),
                Claim(Or(P, Not(P)), steps = ZeroCount),
                Claim(And(Truth, Truth), steps = OneCount),
                Claim(Implies(Falsity, Falsity), steps = ZeroCount),
                Claim(Implies(Falsity, Truth), steps = ZeroCount),
                Claim(Implies(Truth, Truth), steps = ZeroCount),
                Claim(Truth, steps = ZeroCount),
                Claim(Implies(Implies(Truth, Falsity), Falsity), steps = OneCount),
                Claim(Tfae(A, B, C), provable = false),
                Claim(Implies(Or(R, P), Or(And(P, Q), R)), provable = false),
                Claim(Implies(And(Implies(And(A, B), C), Implies(Truth, A)), C), provable = false),
                Claim(Implies(And(
                        Implies(And(A, B), C),
                        Implies(And(A1, A2), A),
                        Implies(And(A11, A12), A1),
                        Implies(Truth, A11),
                        Implies(Truth, A12),
                        Implies(Truth, A2),
                        Implies(And(B1, B2), B),
                        Implies(Truth, B1),
                        Implies(And(E, F), C)),
                        C), provable = false),
                Claim(Implies(A, Or(C, B)), provable = false),
                Claim(Implies(Or(A, B), Or(C, B)), provable = false),
                Claim(Implies(Or(A, B), Or(C, B)), provable = false),
                Claim(Implies(Or(A, B), Implies(A, B)), provable = false),
                Claim(And(Truth, Falsity), provable = false),
                Claim(Falsity, provable = false)
        )

        val a = BV("a")
        val x4 = BV("x4")
        val x3 = BV("x3")
        val y = BV("y")
        val z = BV("z")
        val x2 = BV("x2")
        val x = BV("x")

        val C_ = Pred("C", 1)
        val E_ = Pred("E", 1)
        val f = Fn("f")
        val G = Pred("G", 1)
        val R_ = Pred("R", 1)
        val S = Pred("S", 2)
        val P_ = Pred("P", 1)
        val Q_ = Pred("Q", 1)

        val FOL_CLAIMS = arrayOf(
                Claim(Implies(
                        And(
                                ForAll(x,
                                        formula = Implies(P_(x), Q_(x))),
                                ForSome(y,
                                        formula = Implies(Q_(y), R_(y)))),
                        ForSome(z,
                                formula = Implies(P_(z), R_(z)))
                ),
                        steps = Count(20)),
                Claim(ForAll(a,
                        formula = ForSome(x, x2, x3, x4, y,
                                formula = Implies(And(
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
                        steps = Count(8)),
                Claim(Implies(And(
                        ForSome(x,
                                formula = P_(x)),
                        ForSome(x,
                                formula = Q_(x))
                ), ForSome(y, formula = And(P_(y), Q_(y)))),
                        provable = false),
                Claim(Implies(
                        ForSome(x,
                                formula = P_(x)),
                        ForAll(x,
                                formula = P_(x))),
                        provable = false)
                
                /*

                                   */
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

}

