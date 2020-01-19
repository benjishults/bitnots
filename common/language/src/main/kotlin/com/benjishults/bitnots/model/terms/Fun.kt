package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.intern.InternTableWithOther

/**
 * Returns a FunctionConstructor with the given name and arity.  If one already exists with this name, that one is returned.
 * @param name the name of the function
 * @param arity the arity of the function
 * @return a FunctionConstructor with the given name and arity.  If one already exists with this name, that one is returned.
 */
fun Fn(name: String, arity: Int = 1): FunctionConstructor {
    require(arity > 0)
    return FunctionConstructor.intern(name, arity)
}

/**
 * Returns a FunctionConstructor with the given arity and a unique name similar to [name].
 * @param name the name of the function
 * @param arity the arity of the function
 * @return a FunctionConstructor with the given arity and a unique name similar to [name].
 */
fun FnU(name: String, arity: Int = 1): FunctionConstructor {
    require(arity > 0)
    return FunctionConstructor.new(name, arity)
}

/**
 * Returns a Function of no arguments with the given name.  If a constant already exists with this name, that one is returned.
 * @param name the name of the constant
 * @return a Function of no arguments with the given name.  If a constant already exists with this name, that one is returned.
 */
fun Const(name: String) =
        FunctionConstructor.intern(name, 0)(emptyList())

/**
 * Returns a Function of no arguments with a unique name similar to the given name.
 * @param name the name of the constant
 * @return a Function of no arguments with a unique name similar to the given name.
 */
fun ConstU(name: String) =
        FunctionConstructor.new(name, 0)(emptyList())

/**
 * Represents a simple function term in the language.  A constant is represented by a function of no arguments.
 * @param name the FunctionConstructor for the term
 * @param arguments the arguments in the function term
 */
class Function private constructor(name: FunctionConstructor, var arguments: List<Term<*>>) :
        Term<FunctionConstructor>(name) {

    private val freeVars by lazy { mutableSetOf<FreeVariable>() }
    private var dirtyFreeVars = true

    class FunctionConstructor private constructor(
            name: String,
            val arity: Int = 0
    ) : TermConstructor(name) {

        companion object : InternTableWithOther<FunctionConstructor, Int>(
                { name, arity ->
                    FunctionConstructor(name, arity)
                })

        operator fun invoke(arguments: List<Term<*>>): Function {
            check(arguments.size == arity)
            return Function(this, arguments)
        }

        operator fun invoke(argument: Term<*>): Function {
            check(1 == arity)
            return Function(this, listOf(argument))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>): Function {
            check(2 == arity)
            return Function(this, listOf(arg1, arg2))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>): Function {
            check(3 == arity)
            return Function(this, listOf(arg1, arg2, arg3))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>): Function {
            check(4 == arity)
            return Function(this, listOf(arg1, arg2, arg3, arg4))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>, arg5: Term<*>): Function {
            check(5 == arity)
            return Function(this, listOf(arg1, arg2, arg3, arg4, arg5))
        }
    }

    override fun containsInternal(variable: Variable<*>, sub: Substitution): Boolean =
            getFreeVariables().any {
                it.containsInternal(variable, sub)
            }

    override fun unifyUncached(other: Term<*>, sub: Substitution): Substitution =
            if (sub === NotCompatible)
                sub
            else if (other is Function) {
                if (other.cons === cons) {
                    arguments.foldIndexed(sub) { i, s, t ->
                        Term.unify(t, other.arguments[i], s).takeIf {
                            it !== NotCompatible
                        } ?: return NotCompatible
                    }
                } else {
                    NotCompatible
                }
            } else if (other is FreeVariable)
                Term.unify(other, this, sub)
            else
                NotCompatible

    override fun getFreeVariables(): Set<FreeVariable> =
            if (dirtyFreeVars)
                arguments.fold(freeVars.also {
                    dirtyFreeVars = false
                }) { s: MutableSet<FreeVariable>, t ->
                    s += t.getFreeVariables()
                    s
                }
            else
                freeVars

    //    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> =
    //            arguments.fold(mutableMapOf<FreeVariable, Int>()) { s, t ->
    //                t.getFreeVariablesAndCounts().entries.forEach { (v, c) ->
    //                    s.get(v)?.also {
    //                        s.put(v, c + it)
    //                    } ?: s.put(v, c)
    //                }
    //                s
    //            }

    // simultaneous substitution
    override fun applySub(substitution: Substitution) =
            cons(arguments.map {
                it.applySub(substitution)
            })

    override fun applyPair(pair: Pair<Variable<*>, Term<*>>) =
            cons(arguments.map {
                it.applyPair(pair)
            })

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>) =
            arguments.fold(mutableSetOf<Variable<*>>()) { s, t ->
                s.also {
                    it.addAll(t.getVariablesUnboundExcept(boundVars))
                }
            }

    override fun toString() =
            "(${cons.name}${if (arguments.size == 0) "" else " "}${arguments.joinToString(" ")})"

    override fun equals(other: Any?): Boolean {
        if (other === null)
            return false
        if (other is Function && other.arguments.size == this.arguments.size && other.cons === cons) {
            for (index in 0..this.arguments.size - 1) {
                if (other.arguments[index] != this.arguments[index])
                    return false
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int =
            arguments.toTypedArray().contentHashCode() + cons.name.hashCode()

}
