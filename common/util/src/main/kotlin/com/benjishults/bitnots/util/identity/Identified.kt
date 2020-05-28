package com.benjishults.bitnots.util.identity

interface Identified {
    val id : String
    companion object: Identified {
        override val id = this::class.qualifiedName!!
    }

}
