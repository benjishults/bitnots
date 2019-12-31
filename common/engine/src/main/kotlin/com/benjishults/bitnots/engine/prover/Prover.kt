package com.benjishults.bitnots.engine.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.tableau.strategy.InitializationStrategy
import com.benjishults.bitnots.tableau.strategy.StepStrategy
import com.benjishults.bitnots.tableau.strategy.TableauClosingStrategy

abstract class Prover<T>(
        val target: T,
        val initializationStrategy: InitializationStrategy,
        val closingStrategy: TableauClosingStrategy,
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

class FormulaProver(
        target: Formula<*>,
        initializationStrategy: InitializationStrategy,
        closingStrategy: TableauClosingStrategy,
        stepStrategy: StepStrategy<*>,
        proofConstraints: ProofConstraints
) : Prover<Formula<*>>(target, initializationStrategy, closingStrategy, stepStrategy, proofConstraints) {
    override fun init() {
    }

    override fun step(steps: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isDone() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prove() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ProblemProver(
        target: Problem,
        initializationStrategy: InitializationStrategy,
        closingStrategy: TableauClosingStrategy,
        stepStrategy: StepStrategy<*>,
        proofConstraints: ProofConstraints
) : Prover<Problem>(target, initializationStrategy, closingStrategy, stepStrategy, proofConstraints) {
    override fun prove() {
        val hyps = And(*target.hypotheses.map {
            it.formula
        }.toTypedArray())
        val formulae = target.conjectures.map {
            it.formula
        }.map { conjecture ->
            Implies(hyps, conjecture)
        }
    }

    override fun init() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun step(steps: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isDone() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
