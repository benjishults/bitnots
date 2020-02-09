package com.benjishults.bitnots.test

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isPropositional
import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.closer.SuccessfulTableauProofIndicator
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalStepStrategy
import com.benjishults.bitnots.tableauProver.FolFormulaTableauProver
import com.benjishults.bitnots.tableauProver.PropositionalFormulaProver
import com.benjishults.bitnots.tableauProver.TableauProver

val DEFAULT_MAX_STEPS: Int = 30
val DEFAULT_Q_LIMIT: Int = 6

interface Claim<T : ProofInProgress, in P : Prover<T>> {
    val proofInProgress: T
    val formula: Formula
    fun validate(): ProofProgressIndicator
    fun validateWithProver(prover: P): ProofProgressIndicator
}

/**
 * Not thread safe
 */
abstract class PropositionalClaim(
        final override val formula: Formula
) : Claim<PropositionalTableau, PropositionalFormulaProver> {

    init {
        require(formula.isPropositional())
    }

    override val proofInProgress: PropositionalTableau = PropositionalTableau(
            PropositionalInitializationStrategy.init(
                    PropositionalTableauNode(
                            mutableListOf(formula.createSignedFormula()))))

    override fun validate(): ProofProgressIndicator {
        return validateWithProver(PropositionalFormulaProver(
                PropositionalClosingStrategy(),
                PropositionalStepStrategy()))
    }

}

/**
 * Not thread safe
 */
abstract class FolClaim(
        final override val formula: Formula
) : Claim<FolTableau, FolFormulaTableauProver> {
    abstract val qLimit: Int

    override val proofInProgress: FolTableau = FolTableau(
            PropositionalInitializationStrategy.init(
                    FolTableauNode(mutableListOf(formula.createSignedFormula()))))

    override fun validate(): ProofProgressIndicator {
        return validateWithProver(FolFormulaTableauProver(
                FolUnificationClosingStrategy(),
                FolStepStrategy(qLimit)))
    }

}


interface FalseClaim<T : Tableau<*>, in P : TableauProver<T>> : Claim<T, P> {
    override fun validateWithProver(prover: P): ProofProgressIndicator {

        while (true) {
            prover.searchForFinisher(proofInProgress).let { progressIndicator ->
                if (progressIndicator.isDone())
                    error("Unexpectedly proved ${formula}.")
                else if (!prover.step(proofInProgress))
                    return SuccessfulTableauProofIndicator
            }
        }
    }
}

class FalsePropClaim(formula: Formula) : FalseClaim<PropositionalTableau, PropositionalFormulaProver>,
                                            PropositionalClaim(formula)

class FalseFolClaim(
        formula: Formula,
        override val qLimit: Int = DEFAULT_Q_LIMIT
) : FalseClaim<FolTableau, FolFormulaTableauProver>, FolClaim(formula)

interface TrueClaim<T : Tableau<*>, in P : TableauProver<T>> : Claim<T, P> {
    val maxSteps: Int
    val minSteps: Int
    override fun validateWithProver(prover: P): TableauProofProgressIndicator {

        for (step in maxSteps downTo 1) {
            prover.searchForFinisher(proofInProgress).let { progressIndicator ->
                if (progressIndicator.isDone()) {
                    (maxSteps - step).takeIf {
                        it < minSteps
                    }?.let {
                        error("${formula} is unexpectedly proved before ${it + 1} steps.")
                    } ?: return progressIndicator
                }
            }
            if (!prover.step(proofInProgress))
                error("Failed to prove ${formula} with ${maxSteps} steps.")
        }
        return prover.searchForFinisher(proofInProgress).takeIf {
            it.isDone()
        } ?: error("Failed to prove ${formula} with ${maxSteps} steps.")
    }
}

class TruePropClaim(
        formula: Formula,
        override val maxSteps: Int = DEFAULT_MAX_STEPS,
        override val minSteps: Int = 0
) : TrueClaim<PropositionalTableau, PropositionalFormulaProver>, PropositionalClaim(formula)

class TrueFolClaim(
        formula: Formula,
        override val qLimit: Int = DEFAULT_Q_LIMIT,
        override val maxSteps: Int = DEFAULT_MAX_STEPS,
        override val minSteps: Int = 0
) : TrueClaim<FolTableau, FolFormulaTableauProver>, FolClaim(formula)
