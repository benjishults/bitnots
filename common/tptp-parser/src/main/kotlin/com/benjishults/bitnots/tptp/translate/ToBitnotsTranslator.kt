package com.benjishults.bitnots.tptp.translate

import tptp_parser.SimpleTptpParserOutput
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.theory.language.Language

interface Translator<in In, out Out, in Lang> {
    fun translate(incoming: In, language: Lang) : Out
}

interface ToBitnotsTermTranslator< out Out: Term<*>> : Translator<SimpleTptpParserOutput.Term, Out, Language>

class ToFunctionTranslator : ToBitnotsTermTranslator<Function> {
    override fun translate(incoming: SimpleTptpParserOutput.Term, language: Language): Function {
        TODO()
    }
}

