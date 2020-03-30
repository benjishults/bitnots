package com.benjishults.bitnots.tableau.step

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import java.util.*
import kotlin.Comparator

class GammaStep<T : Tableau<N>, N : TableauNode<N>>(
        private val qLimit: Long,
        override val nodeFactory: (SignedFormula<*>, N) -> N
) : Step<T>,
    TableauStep<N> {

    // TODO make this splice
    override fun apply(pip: T): Boolean {
        val gammas: PriorityQueue<Pair<GammaFormula<*>, N>> =
                PriorityQueue(
                        Comparator<Pair<GammaFormula<*>, N>> { o1: Pair<GammaFormula<*>, N>?,
                                                               o2: Pair<GammaFormula<*>, N>? ->
                            o1!!.first.numberOfApplications.compareTo(o2!!.first.numberOfApplications)
                        })
        pip.root.breadthFirst { node ->
            gammas.addAll(node.newFormulas.filterIsInstance<GammaFormula<*>>().filter {
                it.numberOfApplications <= qLimit
            }.map {
                it to node
            }) // stop if we get to one that is ready to go
            && gammas.first().first.numberOfApplications == 0L
        }
        return gammas.firstOrNull()?.let { (gamma, node) ->
            gamma.numberOfApplications++
            addChildFormulasToNewLeaves(gamma, node)
            true
        } ?: false
    }
}
