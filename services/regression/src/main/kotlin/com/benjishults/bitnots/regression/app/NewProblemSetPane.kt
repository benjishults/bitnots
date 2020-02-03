package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.jfx.CloseableStackPane
import com.benjishults.bitnots.jfx.ClosingStackablePane
import com.benjishults.bitnots.parser.ProblemSet
import javafx.collections.ObservableList

class NewProblemSetPane(items: ObservableList<ProblemSet>, stack: CloseableStackPane) : ClosingStackablePane(stack) {

    override val onApply: () -> Unit = {
        // TODO("Gather info from form, create ProblemSet and add it to item list")
    }

    init {

    }

}
