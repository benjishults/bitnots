import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import org.junit.Assert
import org.junit.Test

class TableauStepCountTests {

    @Test
    fun testProps() {
        testClaims(Claim.PROP_CLAIMS)
    }
    
    @Test
    fun testFols() {
        testClaims(Claim.FOL_CLAIMS)
    }

    private fun testClaims(claims: Array<Claim>) {
        for (claim in claims) {
            Tableau(TableauNode(ArrayList<SignedFormula<out Formula<*>>>().also {
                it.add(claim.formula.createSignedFormula())
            }, null)).also { tableau ->
                for (step in claim.steps downTo 1) {
                    Assert.assertFalse("${claim.formula} is unexpectedly proved before ${claim.steps - step + 1} steps",
                            tableau.isClosed())
                    tableau.step()
                }
                Assert.assertTrue("${claim.formula} is unexpectedly ${"un".takeIf { claim.provable } ?: ""}proved after ${claim.steps} steps",
                        tableau.isClosed() xor !claim.provable)
            }
        }
    }
}
