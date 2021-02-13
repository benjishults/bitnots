package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.util.Match
import com.benjishults.bitnots.util.tree.TreeNodeImpl

class FormulaConstructorToFormulasMap(
    map: MutableMap<FormulaConstructor, MutableList<SimpleSignedFormula<*>>>
) : MutableMap<FormulaConstructor, MutableList<SimpleSignedFormula<*>>> by map {
    /**
     * Modifies(!) and returns the receiver.
     * This does not bother about deduplication.
     */
    fun assimilate(other: FormulaConstructorToFormulasMap): FormulaConstructorToFormulasMap {
        other.forEach { (cons: FormulaConstructor, formList) ->
            if (this[cons]?.addAll(formList) === null) {
                this[cons] = mutableListOf(*formList.toTypedArray())
            }
        }
        return this
    }

    operator fun contains(ssf: SimpleSignedFormula<*>): Boolean {
        return this[ssf.formula.constructor]?.contains(ssf) == true
    }

    operator fun plus(more: List<SimpleSignedFormula<*>>): FormulaConstructorToFormulasMap =
        FormulaConstructorToFormulasMap(mutableMapOf()).also { map ->
            more.forEach { ssf ->
                if (map[ssf.formula.constructor]?.add(ssf) === null) {
                    map[ssf.formula.constructor] = mutableListOf(ssf)
                }
            }
        }.assimilate(this)

}

open class TableauNode<TN : TableauNode<TN>>(
    val newFormulas: MutableList<SignedFormula<*>>,
    parent: TN? = null
) : TreeNodeImpl<TN>(parent) {

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    val simpleFormulasAbove: FormulaConstructorToFormulasMap =
        FormulaConstructorToFormulasMap(mutableMapOf()).let { map ->
            parent?.let { nonNullParent: TN ->
                nonNullParent.simpleFormulasAbove.also {
                    it.assimilate(map.also {
                        nonNullParent.newFormulas
                            .filterIsInstance<SimpleSignedFormula<*>>()
                            .forEach { ssf ->
                                if (map[ssf.formula.constructor]?.add(ssf) === null) {
                                    map[ssf.formula.constructor] = mutableListOf(ssf)
                                }
                            }
                    })
                }
            } ?: map
        }

    val branchClosers by lazy {
        mutableListOf<BranchCloser>()
    }

    fun closer(): Match? = branchClosers.mapNotNull {
        it.getMatchOrNull()
    }
        .firstOrNull()

    override fun toString(): String = buildString {
        if (newFormulas.any { it.sign }) {
            append(
                "Suppose: " + newFormulas.filter {
                    it.sign
                }
                    .map { it.formula }
                    .joinToString("\n") + "\n")
        }
        if (newFormulas.any { !it.sign }) {
            append("Show: " + newFormulas.filter { !it.sign }
                .map { it.formula }
                .joinToString("\n") + "\n")
        }
        if (children.isNotEmpty()) {
            children.joinToString("\n")
        }
    }

}
