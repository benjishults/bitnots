import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.Fn
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class FolTests {

    companion object {
        val MAX_STEPS = 10
    }

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

    val a = BV("a")
    val x4 = BV("x4")
    val x3 = BV("x3")
    val y = BV("y")
    val x2 = BV("x2")
    val x = BV("x")
    val f = Fn("f")

    @Test
    fun testValid() {
        val claims = arrayOf(
                Claim(ForAll(ForSome(Implies(And(Pred("P", 1)(a),
                        Pred("E", 1)(a),
                        Implies(Pred("E", 1)(x),
                                Or(Pred("G", 1)(x),
                                        Pred("S", 2)(x, f(x)))),
                        Implies(Pred("E", 1)(x2),
                                Or(Pred("G", 1)(x2),
                                        Pred("C", 1)(f(x2)))),
                        Implies(Pred("S", 2)(a, y),
                                Pred("P", 1)(y))),
                        Or(And(Pred("P", 1)(x3),
                                Pred("G", 1)(x3)),
                                And(Pred("P", 1)(x4),
                                        Pred("C", 1)(x4)))),
                        x, x2, x3, x4, y), a), steps = 8))

        for (claim in claims) {
            val tableau = Tableau(TableauNode(ArrayList<SignedFormula<out Formula<*>>>().also {
                it.add(claim.formula.createSignedFormula())
            }, null))
            for (step in claim.steps downTo 1) {
                Assert.assertFalse("${claim.formula} is unexpectedly proved before ${claim.steps - step + 1} steps",
                        tableau.isClosed())
                tableau.step()
            }
            Assert.assertTrue("${claim.formula} is unexpectedly unproved after ${claim.steps} steps",
                    tableau.isClosed())
            println("Claim verified in ${claim.steps} steps for ${claim.formula}.")
        }
    }

    @Test
    @Ignore
    fun testFalsifiable() {
        val claims = arrayOf<Claim>()

        for (claim in claims) {
            val tableau = Tableau(TableauNode(ArrayList<SignedFormula<out Formula<*>>>().also {
                it.add(claim.formula.createSignedFormula())
            }, null))
            for (step in claim.steps downTo 1) {
                Assert.assertFalse("${claim.formula} is unexpectedly proved before ${claim.steps - step + 1} steps",
                        tableau.isClosed())
                tableau.step()
            }
            Assert.assertFalse("${claim.formula} is unexpectedly proved after ${claim.steps} steps",
                    tableau.isClosed())
            println("Claim verified as unproved in ${MAX_STEPS} steps for ${claim.formula}.")
        }
    }
}
