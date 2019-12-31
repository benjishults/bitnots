package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.TableauNode

interface InitializationStrategy {
    fun init(node: TableauNode)
}
