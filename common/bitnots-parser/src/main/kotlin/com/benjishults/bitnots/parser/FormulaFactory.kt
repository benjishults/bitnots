package com.benjishults.bitnots.parser

import kotlin.reflect.KClass

data class FormulaFactoryHelper<T: Any>(val t: Map<KClass<T>, (String) -> T>)

class FormulaFactory(val helpers: List<FormulaFactoryHelper<*>>) {

    /**
     * For each concrete subtype "T" of Formula, there must be subclasses of SignedFormula with names NegativeT and PositiveT.
     * These SignedFormula classes must have a constructor that takes no arguments or one that takes a single Formula argument.
     */
    // fun parseFormula(formula: CharSequence, types: Array<String> = arrayOf("propositional")): Formula {

        // var const: Constructor<out Formula>? = null
        // formula.dropWhile {
        //     it.isWhitespace()
        // }.takeWhile {
        //     !it.isWhitespace() && it != '('
        // }.let { cons ->
        //
        //     for (type in types) {
        //         val clazz = try {
        //             Class.forName("com.benjishults.bitnots.model.formulas.${type}$.{cons}")
        //         } catch (e: Exception) {
        //             continue
        //         }
                // for (constructor in clazz.constructors as Array<Constructor<out Formula>>) {
                //
                // }
//                when (leastParameters) {
//                    0 -> return const?.newInstance() ?: throw ClassNotFoundException()
//                    1 -> return const?.newInstance(this) ?: throw ClassNotFoundException()
//                    else -> return clazz.kotlin.objectInstance as SignedFormula<T>
//                }
//             }
//
//         }
//         return Truth
//     }
}
