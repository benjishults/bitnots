package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula

interface InitializingStrategy {
    fun init(node: TableauNode)
}

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

open class PropositionalInitializationStrategy : PropositionalInitializingStrategy {

    override fun applyAllAlphas(node: TableauNode) {
        with(node) {
            while (true) {
                val toAdd: MutableList<SignedFormula<Formula<*>>> = mutableListOf()
                newFormulas.iterator().let {
                    while (it.hasNext()) {
                        val current = it.next()
                        if (current is AlphaFormula) {
                            it.remove()
                            toAdd.addAll(current.generateChildren());
                        }
                    }
                }
                if (toAdd.isEmpty())
                    break
                else
                    newFormulas.addAll(toAdd)
            }
        }
    }

    override fun applyRegularity(node: TableauNode) {
        with(node) {
            newFormulas.iterator().let { iter ->
                while (iter.hasNext()) {
                    iter.next().takeIf {
                        // TODO might want to allow anything here... make this configurable?
                        it is SimpleSignedFormula<*>
                    }.let {
                        if (it in simpleFormulasAbove)
                            iter.remove()
                    }
                }
            }
        }
    }

}
