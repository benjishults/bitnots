package com.benjishults.bitnots.engine

//import com.benjishults.bitnots.engine.proof.FolTableau
//import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.PropositionalTableau
import com.benjishults.bitnots.engine.proof.PropositionalTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.Claim
import com.benjishults.bitnots.theory.NoCount
import org.junit.Assert
import org.junit.Test

class TableauStepCountTest {

    @Test
    fun testProps() {
        testClaims(Claim.PROP_CLAIMS, { n: PropositionalTableauNode ->
            PropositionalTableau(n)
        }) { forms, p ->
            PropositionalTableauNode(forms, p)
        }
    }

//    @Test
//    fun testFols() {
//        testClaims<FolTableauNode, FolTableau>(Claim.FOL_CLAIMS, { n: FolTableauNode ->
//            FolTableau(n)
//        }) { forms, p: FolTableauNode? ->
//            FolTableauNode(forms, p)
//        }
//    }

    private fun <N : TableauNode, T : Tableau> testClaims(
            claims: Array<Claim>,
            tabFactory: (N) -> T,
            nodeFactory: (MutableList<SignedFormula<Formula<*>>>, N?) -> N
    ) {
        for (claim in claims) {
            tabFactory(nodeFactory(ArrayList<SignedFormula<Formula<*>>>().also {
                it.add(claim.formula.createSignedFormula())
            }, null)).also { tableau ->
                if (claim.steps === NoCount) {
                    println("WARN: in some logics, this could run forever.")
                    while (true) {
                        if (tableau.findCloser().isCloser())
                            break
                        else if (!tableau.step())
                            if (claim.provable) {
                                Assert.fail("Failed to prove ${claim.formula} with unlimited steps.")
                            } else
                                break
                    }
                } else {
                    for (step in claim.steps.toInt() downTo 1) {
                        if (tableau.findCloser().isCloser()) {
                            Assert.fail("${claim.formula} is unexpectedly proved before ${claim.steps.toInt() - step + 1} steps")
                        }
                        tableau.step()
                    }
                }
                if (claim.provable) {
                    if (!tableau.findCloser().isCloser()) {
                        Assert.fail("Failed to prove ${claim.formula} with ${claim.steps.toInt()} steps.")
                    }
                } else if (tableau.findCloser().isCloser()) {
                    Assert.fail("Unexpectedly proved ${claim.formula}.")
                }
            }
        }
    }
}
