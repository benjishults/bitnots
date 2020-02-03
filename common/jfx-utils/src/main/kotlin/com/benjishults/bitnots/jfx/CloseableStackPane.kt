package com.benjishults.bitnots.jfx

import javafx.scene.layout.StackPane

abstract class CloseableStackPane : StackPane() {

    abstract val onClose: () -> Unit

}
