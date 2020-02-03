package com.benjishults.bitnots.jfx

import com.benjishults.bitnots.util.collection.pop
import javafx.scene.layout.StackPane

interface StackablePane {

    val stack: StackPane

    fun popMe() {
        stack.children.pop()
    }

}
