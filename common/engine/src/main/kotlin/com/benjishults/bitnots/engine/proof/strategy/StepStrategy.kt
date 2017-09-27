package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau

interface StepStrategy<in T : Tableau> {
    fun step(tableau: T): Boolean
}
