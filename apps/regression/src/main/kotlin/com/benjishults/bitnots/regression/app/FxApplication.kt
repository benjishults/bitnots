package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.regression.userConfigFolder
import javafx.application.Application
import javafx.scene.layout.Region
import javafx.stage.Stage
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class FxApplication : Application() {

    private lateinit var primaryStage: Stage
    private lateinit var uiSettingsFile: Path
    private lateinit var uiProperties: Properties

    init {
        loadUiConfiguration()
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        primaryStage.title = "Bitnots Tester"
        populateScene(primaryStage)
        primaryStage.show()
    }

    private fun loadUiConfiguration() {
        uiSettingsFile = userConfigFolder.resolve("ui-settings.properties")
        if (Files.notExists(uiSettingsFile)) {
            Files.copy(
                    this.javaClass.getResourceAsStream("/ui-settings.properties-sample"),
                    uiSettingsFile)
        }
        // Configurations().fileBased(YAMLConfiguration::class.java, uiSettingsFile.toFile()) // throws ConfigurationException
        loadUserConfig()
    }

    private fun loadUserConfig() {
        Files.newInputStream(uiSettingsFile).use { stream ->
            Properties().let { properties ->
                properties.load(stream)
                uiProperties = properties
            }
        }
    }

    private fun populateScene(primaryStage: Stage) {
        primaryStage.x = uiProperties.getProperty("ui.framePosX", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        primaryStage.y = uiProperties.getProperty("ui.framePosY", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        val width = uiProperties.getProperty("ui.appWidth", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        val height = uiProperties.getProperty("ui.appHeight", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        primaryStage.scene = RegressionMainPane(
                uiProperties,
                width,
                height)
    }

    /**
     * Stop the service thread first.
     */
    override fun stop() {
        writeUiProperties()
    }

    private fun writeUiProperties() {
        uiProperties.put("ui.framePosY", primaryStage.y.toString())
        uiProperties.put("ui.framePosX", primaryStage.x.toString())
        uiProperties.put("ui.appHeight", primaryStage.scene.height.toString())
        uiProperties.put("ui.appWidth", primaryStage.scene.width.toString())
        Files.newOutputStream(uiSettingsFile).use {
            uiProperties.store(it, null)
        }
    }

}
