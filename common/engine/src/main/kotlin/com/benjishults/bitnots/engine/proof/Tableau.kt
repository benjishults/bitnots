package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import java.util.Stack

class Tableau(val root: TableauNode) {

    override fun toString(): String {
        return buildString {
            root.preOrderWithPath { n, path ->
                this.append(path.joinToString("."))
                this.append("\n")
                this.append(n.toString())
                this.append(n.closers.toString())
                this.append("\n")
                this.append("\n")
                false
            }
        }
    }

    fun isClosed() = root.isClosed()

    fun unify(): MultiBranchCloser? {

        return null
    }

    fun step() {
        if (!applyDelta() && !applyBeta()) {
            applyGamma() && unify() !== null
        }
    }

    // TODO make this splice
    private fun applyDelta(): Boolean {
        var delta: DeltaFormula<out VarBindingFormula>? = null
        val node = root.breadthFirst {
            delta = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
            delta !== null
        }
        if (node === null)
            return false
        delta?.let {
            delta ->
            node.newFormulas.remove(delta);
            val leaves = node.allLeaves()
            leaves.map { leaf -> delta.generateChildren().map { leaf.children.add(TableauNode(mutableListOf(it), leaf)) } }
            return true
        } ?: return false
    }

    // TODO make this splice
    private fun applyGamma(): Boolean {
        var gammas = java.util.TreeSet<Pair<GammaFormula<*>, TableauNode>>(Comparator<Pair<GammaFormula<*>, TableauNode>> {
            o1: Pair<GammaFormula<*>, TableauNode>?, o2: Pair<GammaFormula<*>, TableauNode>? ->
            o1!!.first.numberOfApplications.compareTo(o2!!.first.numberOfApplications)
        })
        root.breadthFirst { node ->

            gammas.addAll((node.newFormulas.filter {
                it is GammaFormula<*>
            } as Iterable<GammaFormula<*>>).map {
                it to node
            }) // stop if we get to one that is ready to go
                    && gammas.first().first.numberOfApplications == 0
        }
        gammas.firstOrNull()?.let {
            (gamma, node) ->
            gamma.numberOfApplications++
            val leaves = node.allLeaves()
            leaves.map { leaf ->
                gamma.generateChildren().map {
                    leaf.children.add(TableauNode(mutableListOf(it), leaf))
                }
            }
            return true
        } ?: return false
    }

    private fun applyBeta(): Boolean {
        var beta: BetaFormula<out Formula<*>>? = null
        val node = root.breadthFirst {
            beta = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
            beta !== null
        }
        if (node === null)
            return false
        beta?.let {
            beta ->
            node.newFormulas.remove(beta);
            val leaves = node.allLeaves()
            leaves.map { leaf -> beta.generateChildren().map { leaf.children.add(TableauNode(mutableListOf(it), leaf)) } }
            return true
        } ?: return false
    }

}
