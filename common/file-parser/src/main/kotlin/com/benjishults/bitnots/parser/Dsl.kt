package com.benjishults.bitnots.parser

class AnnotatedFormula {
    
    var name: String = ""

    fun name(init: () -> String) : Unit {
        name = init()
    }

}

fun annotatedFormula(init: AnnotatedFormula.() -> Unit): AnnotatedFormula {
    val annotatedFormula = AnnotatedFormula()
    annotatedFormula.init()
    return annotatedFormula
}

