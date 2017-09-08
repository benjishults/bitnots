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

data class Claim(
        val formula: Formula<*>,
        val provable: Boolean = true,
        val gamma: Int = 0,
        val steps: Int = DEFAULT_MAX_STEPS) {

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
                Claim(Implies(Tfae(A, B, C), Implies(B, A)), steps = 1),
                Claim(Tfae(P, P, P), steps = 2),
                Claim(Implies(And(A, B), Iff(A, B)), steps = 1),
                Claim(Or(Iff(A, B), A, B), steps = 1),
                Claim(Implies(Implies(A, B), Implies(A, B)), steps = 1),
                Claim(Implies(Falsity, P), steps = 0),
                Claim(Implies(P, P), steps = 0),
                Claim(Or(P, Not(P)), steps = 0),
                Claim(And(Truth, Truth), steps = 1),
                Claim(Implies(Falsity, Falsity), steps = 0),
                Claim(Implies(Falsity, Truth), steps = 0),
                Claim(Implies(Truth, Truth), steps = 0),
                Claim(Truth, steps = 0),
                Claim(Implies(Implies(Truth, Falsity), Falsity), steps = 1),
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
                                ForAll(Implies(P_(x), Q_(x)), x),
                                ForSome(Implies(Q_(y), R_(y)), y)),
                        ForSome(Implies(P_(z), R_(z)), z)
                ), steps = 16),
                Claim(ForAll(ForSome(Implies(And(
                        P_(a),
                        E_(a),
                        Implies(E_(x),
                                Or(G(x), S(x, f(x)))),
                        Implies(E_(x2),
                                Or(G(x2), C_(f(x2)))),
                        Implies(S(a, y), P_(y))),
                        Or(
                                And(P_(x3), G(x3)),
                                And(P_(x4), C_(x4)))),
                        x, x2, x3, x4, y), a), steps = 8)
                /*
                 */
        )
    }

}

