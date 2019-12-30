package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.TableauNode

interface InitializingStrategy {
    fun init(node: TableauNode)
}
