package com.benjishults.bitnots.tableau

abstract class AbstractTableau<TN : TableauNode<TN>>(
        override val root: TN
) : Tableau<TN>
