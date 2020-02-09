package com.benjishults.bitnots.model.terms

abstract class TermConstructor(val name: String) {
    override fun equals(other: Any?): Boolean =
            other?.let {
                it::class === this::class && (it as TermConstructor).name == name
            } ?: false

    override fun hashCode() = name.hashCode()

    open operator fun invoke(arguments: List<Term>): Function {
        throw NotImplementedError()
    }

    open operator fun invoke(argument: Term): Function {
        throw NotImplementedError()
    }

    open operator fun invoke(arg1: Term, arg2: Term): Function {
        throw NotImplementedError()
    }

    open operator fun invoke(arg1: Term, arg2: Term, arg3: Term): Function {
        throw NotImplementedError()
    }

    open operator fun invoke(arg1: Term, arg2: Term, arg3: Term, arg4: Term): Function {
        throw NotImplementedError()
    }

    open operator fun invoke(arg1: Term, arg2: Term, arg3: Term, arg4: Term, arg5: Term): Function {
        throw NotImplementedError()
    }

}
