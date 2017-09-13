import com.benjishults.bitnots.engine.proof.FolTableau
import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.PropositionalTableau
import com.benjishults.bitnots.engine.proof.PropositionalTableauNode
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
        testClaims(Claim.PROP_CLAIMS, { n: PropositionalTableauNode ->
            PropositionalTableau<PropositionalTableauNode>(n)
        }) {
            forms, p ->
            PropositionalTableauNode(forms, p)
        }
    }

    @Test
    fun testFols() {
        testClaims<FolTableauNode, FolTableau>(Claim.FOL_CLAIMS, { n: FolTableauNode ->
            FolTableau(n)
        }) {
            forms, p: FolTableauNode? ->
            FolTableauNode(forms, p)
        }
    }

    private fun <N : TableauNode, T : Tableau<N>> testClaims(
            claims: Array<Claim>,
            tabFactory: (N) -> T,
            nodeFactory: (MutableList<SignedFormula<Formula<*>>>, N?) -> N) {
        for (claim in claims) {
            tabFactory(nodeFactory(ArrayList<SignedFormula<Formula<*>>>().also {
                it.add(claim.formula.createSignedFormula())
            }, null)).also { tableau ->
                if (claim.steps !== NoCount) {
                    println("WARN: in some logics, this could run forever.")
                    while (true) {
                        if (tableau.isClosed())
                            break
                        if (!tableau.step())
                            Assert.fail("Failed to prove ${claim.formula} with unlimited steps.")
                    }
                } else {
                    for (step in claim.steps.toInt() downTo 1) {
                        Assert.assertFalse("${claim.formula} is unexpectedly proved before ${claim.steps.toInt() - step + 1} steps",
                                tableau.isClosed())
                        tableau.step()
                    }
                }
            }
        }
    }
}
