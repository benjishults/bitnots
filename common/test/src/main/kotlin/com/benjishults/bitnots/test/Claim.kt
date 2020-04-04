package com.benjishults.bitnots.test

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isPropositional
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tableauProver.PropositionalTableauHarness

val DEFAULT_MAX_STEPS: Long = 30L
val DEFAULT_Q_LIMIT: Long = 6L

interface ExpectOutcome {
    fun validate(proofInProgress: ProofInProgress): Boolean
}

interface Claim<T : ProofInProgress> : ExpectOutcome {
    val formula: Formula
    suspend fun attempt(harness: Harness<T>): ProofInProgress
    suspend fun attempt(): ProofInProgress
}

object FalseClaim : ExpectOutcome {
    override fun validate(proofInProgress: ProofInProgress): Boolean {
        return !proofInProgress.indicator.isDone()
    }
}

class TrueClaim(private val maxSteps: Long, private val minSteps: Long) : ExpectOutcome {
    override fun validate(proofInProgress: ProofInProgress): Boolean {
        return proofInProgress.indicator.isDone() && proofInProgress.getSteps() in minSteps..maxSteps
    }
}

abstract class PropositionalClaim(
    final override val formula: Formula
) : Claim<PropositionalTableau> {

    init {
        require(formula.isPropositional())
    }

    override suspend fun attempt(): ProofInProgress =
        attempt(PropositionalTableauHarness())

    override suspend fun attempt(harness: Harness<PropositionalTableau>): ProofInProgress {
        return PropositionalTableau(
            PropositionalInitializationStrategy.init(
                PropositionalTableauNode(
                    mutableListOf(formula.createSignedFormula())
                )
            )
        ).also {
            harness.prove(it)
        }
    }

}

abstract class FolClaim(
    final override val formula: Formula
) : Claim<FolTableau> {
    abstract val qLimit: Long

    override suspend fun attempt(): ProofInProgress =
        attempt(FolTableauHarness(qLimit))

    override suspend fun attempt(harness: Harness<FolTableau>): ProofInProgress {
        return FolTableau(
            PropositionalInitializationStrategy.init(
                FolTableauNode(mutableListOf(formula.createSignedFormula()))
            )
        ).also { harness.prove(it) }
    }

}

class FalsePropClaim(
    formula: Formula
) : ExpectOutcome by FalseClaim, PropositionalClaim(formula) {
    override fun toString(): String {
        return "{formula=$formula}"
    }
}

class FalseFolClaim(
    formula: Formula,
    override val qLimit: Long = DEFAULT_Q_LIMIT
) : ExpectOutcome by FalseClaim, FolClaim(formula) {
    override fun toString(): String {
        return "{formula=$formula, qLimit=$qLimit}"
    }
}

class TruePropClaim(
    formula: Formula,
    val maxSteps: Long = DEFAULT_MAX_STEPS,
    val minSteps: Long = 0L
) : ExpectOutcome by TrueClaim(maxSteps, minSteps), PropositionalClaim(formula) {
    override fun toString(): String {
        return "{formula=$formula, minSteps=$minSteps, maxSteps=$maxSteps}"
    }
}

class TrueFolClaim(
    formula: Formula,
    override val qLimit: Long = DEFAULT_Q_LIMIT,
    val maxSteps: Long = DEFAULT_MAX_STEPS,
    val minSteps: Long = 0L
) : ExpectOutcome by TrueClaim(maxSteps, minSteps), FolClaim(formula) {
    override fun toString(): String {
        return "{formula=$formula, minSteps=$minSteps, maxSteps=$maxSteps, qLimit=$qLimit}"
    }
}
