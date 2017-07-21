package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTableWithOther

/**
 * @param name the name of the function
 * @param arity the arity of the function
 * @return a FunctionConstructor with the given name and arity.  If one already exists with this name, that one is returned.
 */
fun Fn(name: String, arity: Int = 1) = FunctionConstructor.intern(name, arity)

/**
 * @param name the name of the constant
 * @return a Function of no arguments with the given name name.  If a constant already exists with this name, that one is returned.
 */
fun Const(name: String) = Fn(name, 0)(emptyArray())

/**
 * Represents a simple function term in the language.  A constant is represented by a function of no arguments.
 * @param name the FunctionConstructor for the term
 * @param arguments the arguments in the function term
 */
class Function private constructor(name: FunctionConstructor, var arguments: Array<Term<*>>) : Term<FunctionConstructor>(name) {

    private val freeVars by lazy { mutableSetOf<FreeVariable>() }
    private var dirtyFreeVars = true

    class FunctionConstructor private constructor(name: String, val arity: Int = 0) : TermConstructor(name) {
        companion object : InternTableWithOther<FunctionConstructor, Int>({ name, arity -> FunctionConstructor(name, arity) })

        operator fun invoke(arguments: Array<Term<*>>): Function {
            check(arguments.size == arity)
            return Function(this, arguments)
        }

        operator fun invoke(argument: Term<*>): Function {
            check(1 == arity)
            return Function(this, arrayOf(argument))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>): Function {
            check(2 == arity)
            return Function(this, arrayOf(arg1, arg2))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>): Function {
            check(3 == arity)
            return Function(this, arrayOf(arg1, arg2, arg3))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>): Function {
            check(4 == arity)
            return Function(this, arrayOf(arg1, arg2, arg3, arg4))
        }

        operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>, arg5: Term<*>): Function {
            check(5 == arity)
            return Function(this, arrayOf(arg1, arg2, arg3, arg4, arg5))
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

    override fun unify(other: Term<*>, sub: Substitution): Substitution {
        if (other is Function) {
            if (other.cons === cons) {
                return arguments.foldIndexed(sub) { i, s, t ->
                    t.unify(other.arguments[i], s).takeIf {
                        it !== NotUnifiable
                    } ?: return NotUnifiable
                }
            } else {
                return NotUnifiable
            }
        } else if (other is FreeVariable)
            return other.unify(this, sub)
        else
            return NotUnifiable
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
            cons(arguments.map {
                it.applySub(substitution)
            }.toTypedArray())

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>) =
            arguments.fold(mutableSetOf<Variable<*>>()) { s, t ->
                s.also {
                    it.addAll(t.getVariablesUnboundExcept(boundVars))
                }
            }

    override fun toString() = "(${cons.name}${if (arguments.size == 0) "" else " "}${arguments.joinToString(" ")})"

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other::class === this::class) {
            if ((other as Function).arguments.size == this.arguments.size) {
                for (index in 0..this.arguments.size - 1) {
                    if (other.arguments[index] != this.arguments[index])
                        return false
                }
                return true
            }
        }
        return false
    }
}
