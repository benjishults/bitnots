package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.Tableau

interface TableauClosingStrategy {
    fun populateBranchClosers(tableau: Tableau)
}
