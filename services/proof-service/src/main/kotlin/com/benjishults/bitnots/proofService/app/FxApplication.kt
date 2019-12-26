package com.benjishults.bitnots.proofService.app

import com.benjishults.bitnots.proofService.AppThread
import com.benjishults.bitnots.proofService.ServiceThread
import com.benjishults.bitnots.proofService.app.config.UiConfig
import com.benjishults.bitnots.proofService.control.ProofKickoff
import com.benjishults.bitnots.util.threads.interruptAndJoin
import com.benjishults.bitnots.util.threads.safeSaveAndStartThread
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spi.PropertiesComponent
import org.apache.camel.support.LifecycleStrategySupport
import java.io.File
import java.util.concurrent.CountDownLatch

class FxApplication : Application() {

    private val contextStarted = CountDownLatch(1)
    private lateinit var context: CamelContext
    private lateinit var properties: PropertiesComponent

    private lateinit var uiConfig: UiConfig

    private val fileName = SimpleStringProperty("file")
    private val fileType = SimpleStringProperty("tptp")
    private val fileContents = SimpleStringProperty()
    private lateinit var file: File

    init {
        safeSaveAndStartThread(ServiceThread, Thread {
            context = DefaultCamelContext()
            context.addLifecycleStrategy(object : LifecycleStrategySupport() {
                override fun onContextStart(context: CamelContext?) {
                    super.onContextStart(context)
                    contextStarted.countDown()
                }

                override fun onContextStop(context: CamelContext?) {
                    ServiceThread.set(null)
                    interruptAndJoin(AppThread)
                    super.onContextStop(context)
                }
            })
            wireUpServiceConfiguration()
            // TODO simplify loading of config
            startServiceAsync()
        })
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Theory"
        contextStarted.await()
        uiConfig = UiConfig(
                properties.resolveProperty("ui.monoFontSize")
                        .orElse("12.0").toDouble(),
                properties.resolveProperty("ui.uiFontSize")
                        .orElse("12.0").toDouble())
        context.registry.bind("uiConfig", uiConfig)
        file = FileChooser().run {
            initialDirectory = File(properties.resolveProperty("ui.baseInputFolder").orElse(null))
            title = "Select theory file"
            showOpenDialog(null)
        }
        fileName.value = file.absolutePath
        fileContents.value = file.inputStream().use {
            String(it.readAllBytes())
        }
        primaryStage.scene = TheoryEditor(uiConfig, file)
        primaryStage.show()
    }

    private fun startServiceAsync() {
        context.addRoutes(ProofKickoff())
        context.start()
    }

    private fun wireUpServiceConfiguration() {
        properties = context.propertiesComponent
        properties.addLocation("classpath:ui.properties")
        properties.setIgnoreMissingLocation(false)
    }

    /**
     * Stop the service thread first.
     */
    override fun stop() {
        // TODO give warning message in UI
        AppThread.set(null)
        interruptAndJoin(ServiceThread)
    }

}
