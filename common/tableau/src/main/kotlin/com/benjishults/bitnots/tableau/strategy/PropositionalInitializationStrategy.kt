package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.inference.SimpleSignedFormula

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
