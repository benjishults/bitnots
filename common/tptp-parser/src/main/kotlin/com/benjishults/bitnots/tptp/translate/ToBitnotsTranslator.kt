package com.benjishults.bitnots.tptp.translate

import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Term

interface Translator<in In, out Out, in Lang> {
    fun translate(incoming: In, language: Lang) : Out
}

