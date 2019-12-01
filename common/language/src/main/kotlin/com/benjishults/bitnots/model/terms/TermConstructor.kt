package com.benjishults.bitnots.model.terms

open class TermConstructor(val name: String) {
    override fun equals(other: Any?): Boolean =
            other?.let {
                it::class === this::class && (it as TermConstructor).name == name
            } ?: false

    override fun hashCode() = name.hashCode()

}
