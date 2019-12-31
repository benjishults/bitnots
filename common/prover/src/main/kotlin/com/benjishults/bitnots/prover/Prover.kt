package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.prover.strategy.InitializationStrategy
import com.benjishults.bitnots.prover.strategy.StepStrategy

abstract class Prover<T, N, P: ProofInProgress, I: ProofProgressIndicator>(
        val target: T,
        val initializationStrategy: InitializationStrategy<N>,
        val closingStrategy: FinishingStrategy<P,  I>,
        val stepStrategy: StepStrategy<*>,
        val proofConstraints: ProofConstraints
) {
    abstract fun init()
    abstract fun step(steps: Long = 1)
    abstract fun isDone()
    /**
     * Prove as far as allowed by [proofConstraints]
     */
    abstract fun prove()
}

