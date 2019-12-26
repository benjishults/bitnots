package com.benjishults.bitnots.proofService.app.config

import javafx.scene.text.Font

data class UiConfig(val monoFontSize: Double, val uiFontSize: Double) {

    fun getMonoFont() = Font.font("mono", monoFontSize)

    fun getUiFont() = Font.font("sans", uiFontSize)

}
