package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.regression.AppThread
import com.benjishults.bitnots.regression.ServiceThread
import com.benjishults.bitnots.regression.config.ProofKickoff
import com.benjishults.bitnots.util.threads.interruptAndJoin
import com.benjishults.bitnots.util.threads.safeSaveAndStartThread
import javafx.application.Application
import javafx.scene.layout.Region
import javafx.stage.Stage
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.support.LifecycleStrategySupport
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CountDownLatch

class FxApplication : Application() {

    private val contextStarted = CountDownLatch(1)
    private lateinit var context: CamelContext
    private lateinit var primaryStage: Stage
    private lateinit var uiSettingsFile: Path
    private lateinit var uiProperties: Properties

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
            context.registry.bind("fxApplication", this)
            context.propertiesComponent.setIgnoreMissingLocation(false)
            context.propertiesComponent.addLocation("classpath:/application.properties")
            startServiceAsync()
        })
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        primaryStage.title = "Bitnots Tester"
        contextStarted.await()
        wireUpUiConfiguration()
        applyCustomProperties(primaryStage)
        // context.registry.bind("theoryEditor", primaryStage.scene)
        primaryStage.show()
    }

    private fun applyCustomProperties(primaryStage: Stage) {
        primaryStage.x = uiProperties.getProperty("ui.framePosX", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        primaryStage.y = uiProperties.getProperty("ui.framePosY", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        val width = uiProperties.getProperty("ui.appWidth", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        val height = uiProperties.getProperty("ui.appHeight", Region.USE_COMPUTED_SIZE.toString()).toDouble()
        primaryStage.scene = RegressionMainPane(
                context,
                uiProperties,
                width,
                height)
    }

    private fun startServiceAsync() {
        context.addRoutes(ProofKickoff())
        context.start()
    }

    private fun wireUpUiConfiguration() {
        try {
            val folder = Paths.get(System.getProperty("user.home"), ".bitnots")
            Files.createDirectories(folder)
            uiSettingsFile = folder.resolve("ui-settings.properties")
            // context.propertiesComponent.addLocation("file:${uiSettingsFile}")
            loadUserConfig()
        } catch (e: NoSuchFileException) {
            if (Files.notExists(uiSettingsFile)) {
                Files.copy(
                        this.javaClass.getResourceAsStream("/ui-settings.properties-sample"),
                        uiSettingsFile)
            }
            loadUserConfig()
        }
    }

    private fun loadUserConfig() {
        Files.newInputStream(uiSettingsFile).use { stream ->
            Properties().let { properties ->
                properties.load(stream)
                uiProperties = properties
            }
        }
    }

    /**
     * Stop the service thread first.
     */
    override fun stop() {
        writeUiProperties()
        // TODO give warning message in UI
        AppThread.set(null)
        interruptAndJoin(ServiceThread)
    }

    private fun writeUiProperties() {
        // uiProperties.keys.forEach { objectKey ->
        //     (objectKey as String).let { key ->
        //         context.propertiesComponent.resolveProperty(key).ifPresent { value ->
        //             uiProperties.put(key, value)
        //         }
        //     }
        // }
        uiProperties.put("ui.framePosY", primaryStage.y.toString())
        uiProperties.put("ui.framePosX", primaryStage.x.toString())
        uiProperties.put("ui.appHeight", primaryStage.scene.height.toString())
        uiProperties.put("ui.appWidth", primaryStage.scene.width.toString())
        Files.newOutputStream(uiSettingsFile).use {
            uiProperties.store(it, null)
        }
    }

}
