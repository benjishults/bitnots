package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.strategy.TableauClosingStrategy
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import kotlin.coroutines.coroutineContext

interface TableauProver<T : Tableau<*>> : Prover<T> {
    val finishingStrategy: TableauClosingStrategy<T>
    val stepStrategy: StepStrategy<T>
    // var indicator: TableauProofProgressIndicator
    // val context: FormulaProofTarget

    /**
     * Expand the tableau a bit.
     */
    fun step(proofInProgress: T): Boolean = stepStrategy.step(proofInProgress)

    // fun findCloser() = finishingStrategy.searchForClosure(proofInProgress)

    /**
     * Check whether the tableau can be closed.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun searchForFinisher(proofInProgress: T): TableauProofProgressIndicator {
        return finishingStrategy.searchForClosure(proofInProgress)
    }

    override fun isDone(proofInProgress: T) = proofInProgress.closer().isDone()

    override suspend fun prove(
            proofInProgress: T
    ): TableauProofProgressIndicator {
        while (coroutineContext.isActive) {
            val indicator = searchForFinisher(proofInProgress)
            if (indicator.isDone()) {
                return indicator
            } else if (!coroutineContext.isActive) {
                break
            } else if (!step(proofInProgress)) {
                return indicator
            }
        }
        yield()
        error("Impossible!")
    }

}

// TODO change name of module to prover
// TODO move proof code from tableau to here

// class TableauSimpleProblemProver(
//         target: Problem,
//         override val closingStrategy: FolUnificationClosingStrategy,
//         stepStrategy: StepStrategy<*>,
//         val closedIndicatorFactory: (TableauNode) -> InProgressTableauClosedIndicator,
//         proofConstraints: ProofConstraints
// ) : Prover<Problem, TableauNode, Tableau, InProgressTableauClosedIndicator>(
//         target,
//         closingStrategy,
//         stepStrategy,
//         proofConstraints
// ) {
//
//     val tableau: Tableau = FolTableau(
//             FolTableauNode(mutableListOf(target.conjectures.first().formula.createSignedFormula())))
//
//     override fun prove() {
//         val hyps = And(*target.hypotheses.map {
//             it.formula
//         }.toTypedArray())
//         val formulae = target.conjectures.map {
//             it.formula
//         }.map { conjecture ->
//             Implies(hyps, conjecture)
//         }
//     }
//
//     fun prover(): InProgressTableauClosedIndicator { // = root.isClosed()
//         // TODO could this indicate that not all branches have branch-closers?
//         closingStrategy.populateBranchClosers(tableau)
//         // will this ever exceed size 1?
//         val toBeExtended = Stack<InProgressTableauClosedIndicator>().apply {
//             push(closedIndicatorFactory(tableau.root))
//         }
//         do {
//             toBeExtended.pop().let { toExtend ->
//                 toExtend.nextNode().branchClosers.forEach { bc ->
//                     toExtend.createExtension(bc).let { ext ->
//                         if (ext !== NotCompatible) {
//                             ext.takeIf {
//                                 it.isDone()
//                             }?.let {
//                                 return it
//                             } ?: toBeExtended.push(ext) // this means I'll come back to it
//                         }
//                     }
//                 }
//                 toExtend.progress().takeIf {
//                     it !== NotCompatible
//                 }?.let {
//                     toBeExtended.push(it)
//                 } // toExtend cannot be extended ?: null
//             }
//         } while (toBeExtended.isNotEmpty());
//         return NotCompatible
//     }
//
//     override fun init() {
//         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//     }
//
//     override fun step(steps: Long) {
//         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//     }
//
//     override fun isDone() {
//         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//     }
// }
