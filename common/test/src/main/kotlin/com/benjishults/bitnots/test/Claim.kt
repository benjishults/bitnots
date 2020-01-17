package com.benjishults.bitnots.test

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isPropositional
import com.benjishults.bitnots.prover.ProofConstraints
import com.benjishults.bitnots.prover.ProofMonitor
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.closer.BooleanClosedIndicator
import com.benjishults.bitnots.tableau.closer.Done
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingClosedIndicator
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

interface Claim<in P : ProofMonitor<*>> {
    val formula: Formula<*>
    fun validate(): ProofProgressIndicator
    fun validateWithProver(prover: P): ProofProgressIndicator
}

interface PropositionalClaim : Claim<PropositionalFormulaProver> {
    override fun validate(): ProofProgressIndicator =
            validateWithProver(PropositionalFormulaProver(
                    formula,
                    PropositionalClosingStrategy { BooleanClosedIndicator(it) },
                    PropositionalStepStrategy { l, n ->
                        PropositionalInitializationStrategy.init(PropositionalTableauNode(mutableListOf(l), n))
                    }))
}

interface FolClaim : Claim<FolFormulaTableauProver> {
    val constraints: ProofConstraints
    override fun validate(): ProofProgressIndicator =
            validateWithProver(FolFormulaTableauProver(
                    formula,
                    FolUnificationClosingStrategy({ UnifyingClosedIndicator(it) }),
                    FolStepStrategy(constraints.maxSteps) { l, n ->
                        PropositionalInitializationStrategy.init(FolTableauNode(mutableListOf(l), n))
                    }))
}


interface FalseClaim<in P : TableauProver<*, *>> : Claim<P> {
    override fun validateWithProver(prover: P): ProofProgressIndicator {
        while (true) {
            prover.searchForFinisher().let { progressIndicator ->
                if (progressIndicator.isDone())
                    error("Unexpectedly proved ${formula}.")
                else if (!prover.step())
                    return Done
            }
        }
    }
}

class FalsePropClaim(override val formula: Formula<*>) : FalseClaim<PropositionalFormulaProver>, PropositionalClaim {
    init {
        require(formula.isPropositional())
    }
}

class FalseFolClaim(
        override val formula: Formula<*>,
        override val constraints: ProofConstraints = ProofConstraints(qLimit = DEFAULT_Q_LIMIT)
) : FalseClaim<FolFormulaTableauProver>, FolClaim

interface TrueClaim<in P : TableauProver<*, *>> : Claim<P> {
    val constraints: ProofConstraints

    override fun validateWithProver(prover: P): TableauProofProgressIndicator {

        for (step in constraints.maxSteps downTo 1) {
            prover.searchForFinisher().let { progressIndicator ->
                if (progressIndicator.isDone()) {
                    (constraints.maxSteps - step).takeIf {
                        it < constraints.minSteps
                    }?.let {
                        error("${formula} is unexpectedly proved before ${it + 1} steps.")
                    } ?: return progressIndicator
                }
            }
            if (!prover.step())
                error("Failed to prove ${formula} with ${constraints.maxSteps} steps.")
        }
        return prover.searchForFinisher().takeIf {
            it.isDone()
        } ?: error("Failed to prove ${formula} with ${constraints.maxSteps} steps.")
    }
}

class TruePropClaim(
        override val formula: Formula<*>,
        override val constraints: ProofConstraints = ProofConstraints(0, DEFAULT_MAX_STEPS, DEFAULT_Q_LIMIT)
) : TrueClaim<PropositionalFormulaProver>, PropositionalClaim {
    init {
        require(formula.isPropositional())
    }
}

class TrueFolClaim(
        override val formula: Formula<*>,
        override val constraints: ProofConstraints = ProofConstraints(0, DEFAULT_MAX_STEPS)
) : TrueClaim<FolFormulaTableauProver>, FolClaim
