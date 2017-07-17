package com.benjishults.bitnots.model.terms

open class TermConstructor(val name: String) {
    override fun equals(other: Any?): Boolean {
        other?.let {
            return it::class === this::class && (it as TermConstructor).name == name
        } ?: return false
    }

    override fun hashCode() = name.hashCode()

}
