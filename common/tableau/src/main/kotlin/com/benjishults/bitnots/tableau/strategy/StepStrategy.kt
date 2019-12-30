package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.Tableau

interface StepStrategy<in T : Tableau> {
    fun step(tableau: T): Boolean
}
