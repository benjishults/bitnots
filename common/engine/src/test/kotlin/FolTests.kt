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

	val A = Prop.intern("A")
	val A1 = Prop.intern("A1")
	val A2 = Prop.intern("A2")
	val A11 = Prop.intern("A11")
	val A12 = Prop.intern("A12")
	val B = Prop.intern("B")
	val B1 = Prop.intern("B1")
	val B2 = Prop.intern("B2")
	val C = Prop.intern("C")
	val E = Prop.intern("E")
	val F = Prop.intern("F")
	val P = Prop.intern("P")
	val Q = Prop.intern("Q")
	val R = Prop.intern("R")

	@Test
	fun testValid() {
		/*
   (forall ((a))
    (for-some ((x) (x2) (x3) (x4) (y))
      (implies (and (p a) (e a)
                    (implies (e x)
                      (or (g x) (s x (f x))))
                    (implies (e x2)
                      (or (g x2) (c (f x2)))) 
                    (implies (s a y) (p y))) 
        (or (and (p x3) (g x3)) 
            (and (p x4) (c x4)))))))

		 */
		val claims = arrayOf(
				Claim(ForAll(ForSome(Implies(And(Pred("P", BV("a")),
						Pred("E", BV("a")),
						Implies(Pred("E", BV("x")),
								Or(Pred("G", BV("x")), Pred("S", BV("x"), Fn("f")(BV("x"))))),
						Implies(Pred("E", BV("x2")),
								Or(Pred("G", BV("x2")), Pred("C", Fn("f")(BV("x2"))))),
						Implies(Pred("S", BV("a"), BV("y")),
								Pred("P", BV("y")))),
						Or(And(Pred("P", BV("x3")), Pred("G", BV("x3"))),
								And(Pred("P", BV("x4")), Pred("C", BV("x4"))))),
						BV("x"), BV("x2"), BV("x3"), BV("x4"), BV("y")), BV("a")), steps = 8))

		for (claim in claims) {
			val tableau = Tableau(TableauNode(ArrayList<SignedFormula<out Formula>>().also {
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
		val claims = arrayOf(
				// make sure condense is sound
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

		for (claim in claims) {
			val tableau = Tableau(TableauNode(ArrayList<SignedFormula<out Formula>>().also {
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
