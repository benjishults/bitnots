package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator

interface Tableau {

    val root: TableauNode
    /**
     * Attempts to find or create an InProgressTableauClosedIndicator that closes the entire tree.
     */
    fun findCloser(): InProgressTableauClosedIndicator

    /**
     * Returns true if the step made a change to the receiver.
     */
    fun step(): Boolean

    /**
     * gets the latest found closer
     */
    fun getCloser(): InProgressTableauClosedIndicator

}
