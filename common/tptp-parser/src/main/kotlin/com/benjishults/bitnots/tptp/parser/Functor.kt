package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term

data class Functor(val cons: String, val args: List<Functor>) {
    fun toFormula(bvs: Set<BoundVariable>): Formula =
            if (args.isEmpty())
                Prop(cons)/*, Atomic*/
            else
                args.map { it.toTerm(bvs) }.toList().let {
                    Pred(cons, it.size)(it)/*, Atomic*/
                }


    fun toTerm(bvs: Set<BoundVariable>):  Term =
            if (args.isEmpty()) {
                if (cons.first().isUpperCase()) {
                    if (cons in bvs.map { it.cons.name }) {
                        BV(cons)
                    } else {
                        FV(cons)
                    }
                } else
                    Const(cons)
            } else
                args.map { it.toTerm(bvs) }.toList().let {
                    Fn(cons, it.size)(it)
                }

    companion object {
        fun parse(tokenizer: TptpTokenizer): Functor =
                tokenizer.popToken().let { functor ->
                    functor.toIntOrNull()?.let {
                        Functor(functor, emptyList())
                    } ?: run {
                        require(functor.first().isLetter()) {
                            tokenizer.finishMessage("Expected functor when parsing '$functor'")
                        }
                        Functor(functor, generateSequence(tokenizer.peek().let {
                            if (it == "(") {
                                tokenizer.popToken()
                                parse(tokenizer)
                            } else
                                null
                        }) {
                            tokenizer.popToken().let {
                                if (it == ",") {
                                    parse(tokenizer)
                                } else if (it == ")") {
                                    null
                                } else
                                    error(tokenizer.finishMessage("Expecting ',' or '), but found '$it'"))
                            }
                        }.toList())
                    }
                }
    }
}
