package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.fol.Predicate.PredicateConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.intern.InternTableWithOther


/**
 * Returns a FunctionConstructor with the given name and arity.  If one already exists with this name, that one is returned.
 * @param name the name of the predicate
 * @param arity the arity of the predicate. Must be > 0.  Otherwise, you want a PropositionalVariable.
 * @return a FunctionConstructor with the given name and arity.  If one already exists with this name, that one is returned.
 */
fun Pred(name: String, arity: Int = 1): PredicateConstructor {
    require(arity > 0)
    return PredicateConstructor.intern(name, arity)
}

/**
 * Returns a PredicateConstructor with a unique name close to the given name.
 * @param name the name of the predicate
 * @param arity the arity of the predicate. Must be > 0.  Otherwise, you want a PropositionalVariable.
 * @return a PredicateConstructor with a unique name close to the given name.
 */
fun PredU(name: String, arity: Int = 1): PredicateConstructor {
    require(arity > 0)
    return PredicateConstructor.new(name, arity)
}

/**
 * Represents a simple function term in the language.  A constant is represented by a function of no arguments.
 * @param name the FunctionConstructor for the term
 * @param arguments the arguments in the function term
 */
class Predicate private constructor(name: PredicateConstructor, var arguments: List<Term<*>>) : Formula<PredicateConstructor>(name) {

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
        TODO()
    }

    private val freeVars by lazy { mutableSetOf<FreeVariable>() }
    private var dirtyFreeVars = true

    open class PredicateConstructor(name: String, val arity: Int = 1) : FormulaConstructor(name) {
        companion object : InternTableWithOther<PredicateConstructor, Int>({ name, arity -> PredicateConstructor(name, arity) })

        operator fun invoke(arguments: List<Term<*>>): Predicate {
            check(arguments.size == arity)
            return Predicate(this, arguments)
        }

        operator fun invoke(argument: Term<*>): Predicate {
            check(1 == arity)
            return Predicate(this, listOf(argument))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>): Predicate {
            check(2 == arity)
            return Predicate(this, listOf(arg1, arg2))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>): Predicate {
            check(3 == arity)
            return Predicate(this, listOf(arg1, arg2, arg3))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>): Predicate {
            check(4 == arity)
            return Predicate(this, listOf(arg1, arg2, arg3, arg4))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>, arg5: Term<*>): Predicate {
            check(5 == arity)
            return Predicate(this, listOf(arg1, arg2, arg3, arg4, arg5))
        }
    }

    /**
     * @param variable must not be bound by sub
     */
    override fun contains(variable: Variable<*>, sub: Substitution): Boolean {
        return getFreeVariables().any {
            it.contains(variable, sub)
        }
    }

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution {
        if (other is Predicate) {
            if (other.constructor === constructor) {
                return arguments.foldIndexed(sub) { i, s, t ->
                    Term.unify(t, other.arguments[i], s).takeIf {
                        it !== NotCompatible
                    } ?: return NotCompatible
                }
            } else {
                return NotCompatible
            }
        } else
            return NotCompatible
    }

    override fun getFreeVariables(): Set<FreeVariable> {
        return arguments.takeIf {
            dirtyFreeVars
        }?.fold(freeVars.also {
            dirtyFreeVars = false
        }) { s, t ->
            s += t.getFreeVariables()
            s
        } ?: freeVars
    }

//    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> =
//            arguments.fold(mutableMapOf<FreeVariable, Int>()) { s, t ->
//                t.getFreeVariablesAndCounts().entries.forEach { (v, c) ->
//                    s.get(v)?.also {
//                        s.put(v, c + it)
//                    } ?: s.put(v, c)
//                }
//                s
//            }

    override fun applySub(substitution: Substitution) =
            constructor(arguments.map {
                it.applySub(substitution)
            })

//    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>) =
//            arguments.fold(mutableSetOf<Variable<*>>()) { s, t ->
//                s.also {
//                    it.addAll(t.getVariablesUnboundExcept(boundVars))
//                }
//            }

    override fun toString() = "(${constructor.name}${if (arguments.size == 0) "" else " "}${arguments.joinToString(" ")})"

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other::class === this::class) {
            if ((other as Predicate).arguments.size == this.arguments.size && other.constructor === constructor) {
                for (index in 0..this.arguments.lastIndex) {
                    if (other.arguments[index] != this.arguments[index])
                        return false
                }
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int = arguments.toTypedArray().contentHashCode() + this.constructor.name.hashCode()

}
