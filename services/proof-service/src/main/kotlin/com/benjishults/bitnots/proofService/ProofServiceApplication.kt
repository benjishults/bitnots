package com.benjishults.bitnots.proofService

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

// @Configuration
// @Import()
class ProofServiceApplication : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.setTitle("Hello World!")
        val btn = Button()
        btn.text = "Say 'Hello World'"
        btn.setOnAction {
            println("Hello World!")
        }
        val root: StackPane = StackPane()
        root.children.add(btn);
        primaryStage.scene = Scene(root, 300.0, 250.0)
        primaryStage.show();

    }
}

fun main(args: Array<String>) {
    // Main.main(*args)
    Application.launch(*args)
}
