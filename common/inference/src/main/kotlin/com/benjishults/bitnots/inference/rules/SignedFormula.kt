package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula
import java.lang.reflect.Constructor

abstract class SignedFormula<F : Formula<*>>(val formula: F, val sign: Boolean = false) {
    abstract fun generateChildren(): List<SignedFormula<out Formula<*>>>
    override fun toString() = (if (sign) "Suppose: " else "Show: ") + formula
    override fun equals(other: Any?): Boolean {
        if (other === null)
            return false
        if (other::class === this::class)
            return (other as SignedFormula<*>).formula == formula
        return false
    }
}

/**
 * For each concrete subtype "T" of Formula, there must be subclasses of SignedFormula with names NegativeT and PositiveT.
 * These SignedFormula classes must have a constructor that takes no arguments or one that takes a single Formula argument.
 */
fun Formula<*>.createSignedFormula(sign: Boolean = false): SignedFormula<out Formula<*>> {
    val signString = if (sign) "Positive" else "Negative"
    var leastParameters = Int.MAX_VALUE
    var const: Constructor<SignedFormula<out Formula<*>>>? = null
    val clazz = Class.forName("com.benjishults.bitnots.inference.rules.concrete.${signString}${this::class.simpleName}")
    for (constructor in clazz.constructors as Array<Constructor<SignedFormula<out Formula<*>>>>) {
        if (leastParameters > constructor.parameterCount) {
            leastParameters = constructor.parameterCount
            const = constructor
        }
    }
    when (leastParameters) {
        0 -> return const?.newInstance() ?: throw ClassNotFoundException()
        1 -> return const?.newInstance(this) ?: throw ClassNotFoundException()
        else -> return clazz.kotlin.objectInstance as SignedFormula<out Formula<*>>
    }
}
