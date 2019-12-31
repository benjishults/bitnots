package com.benjishults.bitnots.prover.strategy

interface InitializationStrategy<in N> {
    fun init(node: N)
}
