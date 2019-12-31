package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.prover.Problem
import com.benjishults.bitnots.prover.ProofConstraints
import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.strategy.InitializationStrategy
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.strategy.TableauClosingStrategy

class FormulaProver(
        target: Formula<*>,
        initializationStrategy: InitializationStrategy<TableauNode>,
        closingStrategy: TableauClosingStrategy,
        stepStrategy: StepStrategy<*>,
        proofConstraints: ProofConstraints
) : Prover<Formula<*>, TableauNode, Tableau, InProgressTableauClosedIndicator>(target, initializationStrategy, closingStrategy, stepStrategy, proofConstraints) {
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

// TODO change name of module to prover
// TODO move proof code from tableau to here

class ProblemProver(
        target: Problem,
        initializationStrategy: InitializationStrategy<TableauNode>,
        closingStrategy: TableauClosingStrategy,
        stepStrategy: StepStrategy<*>,
        proofConstraints: ProofConstraints
) : Prover<Problem, TableauNode, Tableau, InProgressTableauClosedIndicator>(target, initializationStrategy, closingStrategy, stepStrategy, proofConstraints) {
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
