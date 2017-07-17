import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula
import com.benjishults.bitnots.model.proof.Tableau
import com.benjishults.bitnots.model.proof.TableauNode
import org.junit.Assert
import org.junit.Test

class PropositionalTests {

	companion object inner {
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
	fun testTautologies() {
		val claims = arrayOf(
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
				Claim(Implies(Implies(Truth, Falsity), Falsity), steps = 1)
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
			Assert.assertTrue("${claim.formula} is unexpectedly unproved after ${claim.steps} steps",
					tableau.isClosed())
			println("Claim verified in ${claim.steps} steps for ${claim.formula}.")
		}
	}

	@Test
	fun testUntruths() {
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
