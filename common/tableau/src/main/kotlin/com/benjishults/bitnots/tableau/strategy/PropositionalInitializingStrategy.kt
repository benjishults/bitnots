package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.TableauNode

// TODO refactor
interface PropositionalInitializingStrategy : InitializingStrategy {
    override fun init(node: TableauNode) {
        with(node) {
            applyAllAlphas(node)
            applyRegularity(node)
//            allFormulas.addAll(newFormulas)
        }
    }

    fun applyAllAlphas(node: TableauNode)
    fun applyRegularity(node: TableauNode)
}
