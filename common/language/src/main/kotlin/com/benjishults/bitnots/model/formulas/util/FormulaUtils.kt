package com.benjishults.bitnots.model.formulas.util

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.AtomicPropositionalFormula
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or

fun Formula<*>.isAtomic(): Boolean = this is Predicate ||
        this is AtomicPropositionalFormula

fun Formula<*>.isLiteral(): Boolean = isAtomic() || (this is Not && this.argument.isAtomic())

fun Formula<*>.isClause(): Boolean = isLiteral() ||
        (this is Or &&
                this.formulas.all {
                    it.isLiteral()
                })

fun Formula<*>.isCnf(): Boolean = isClause() ||
        (this is And &&
                this.formulas.all {
                    it.isClause()
                })

