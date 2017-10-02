package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau

interface TableauClosingStrategy {
    fun populateBranchClosers(tableau: Tableau)
}
