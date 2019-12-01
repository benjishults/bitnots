package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.engine.proof.closer.NotCompatible
import com.benjishults.bitnots.engine.proof.strategy.StepStrategy
import com.benjishults.bitnots.engine.proof.strategy.TableauClosingStrategy
import java.util.*

interface Tableau {

    val root: TableauNode
    /**
     * Attempts to find or create an InProgressTableauClosedIndicator that closes the entire tree.
     */
    fun findCloser(): InProgressTableauClosedIndicator

    /**
     * Returns true if the step made a change to the receiver.
     */
    fun step(): Boolean

    /**
     * gets the latest found closer
     */
    fun getCloser(): InProgressTableauClosedIndicator

}

abstract class AbstractTableau(
    override val root: AbstractTableauNode,
    private val nodeClosingStrategy: TableauClosingStrategy,
    val closedIndicatorFactory: (TableauNode) -> InProgressTableauClosedIndicator,
    private val stepStrategy: StepStrategy<AbstractTableau>
) : Tableau {

    private var closer: InProgressTableauClosedIndicator = NotCompatible

    override fun getCloser(): InProgressTableauClosedIndicator =        closer

    protected fun setCloser(closer: InProgressTableauClosedIndicator) {
        this.closer = closer
    }

    override fun findCloser(): InProgressTableauClosedIndicator { // = root.isClosed()
        nodeClosingStrategy.populateBranchClosers(this)  // TODO could this indicate that not all branches have branch-closers?
        // will this ever exceed size 1?
        val toBeExtended = Stack<InProgressTableauClosedIndicator>().apply { push(closedIndicatorFactory(root)) }
        do {
            toBeExtended.pop().let { toExtend ->
                toExtend.nextNode().branchClosers.forEach { bc ->
                    toExtend.createExtension(bc).let { ext ->
                        if (ext !== NotCompatible) {
                            ext.takeIf {
                                it.isCloser()
                            }?.let {
                                return it
                            } ?: toBeExtended.push(ext) // this means I'll come back to it
                        }
                    }
                }
                toExtend.progress().takeIf {
                    it !== NotCompatible
                }?.let {
                    toBeExtended.push(it)
                } // toExtend cannot be extended ?: null
            }
        } while (toBeExtended.isNotEmpty());
        return NotCompatible
    }

    override fun step(): Boolean =
        stepStrategy.step(this)
}
