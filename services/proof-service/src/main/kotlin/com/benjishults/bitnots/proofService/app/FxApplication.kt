package com.benjishults.bitnots.proofService.app

import com.benjishults.bitnots.proofService.AppThread
import com.benjishults.bitnots.proofService.ServiceThread
import com.benjishults.bitnots.proofService.control.ProofKickoff
import com.benjishults.bitnots.util.threads.interruptAndJoin
import com.benjishults.bitnots.util.threads.safeSaveAndStartThread
import javafx.application.Application
import javafx.stage.Stage
import org.apache.camel.CamelContext
import org.apache.camel.RuntimeCamelException
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spi.PropertiesComponent
import org.apache.camel.support.LifecycleStrategySupport
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch

class FxApplication : Application() {

    private val contextStarted = CountDownLatch(1)
    private lateinit var context: CamelContext
    private lateinit var properties: PropertiesComponent

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
        primaryStage.scene = TheoryEditor(context)
        primaryStage.show()
    }

    private fun startServiceAsync() {
        context.addRoutes(ProofKickoff())
        context.start()
    }

    private fun wireUpServiceConfiguration() {
        properties = context.propertiesComponent
        properties.setIgnoreMissingLocation(false)
        properties.addLocation("classpath:ui.properties")
        try {
            addUserConfigToContext()
        } catch (e: RuntimeCamelException) {
            val folder = Path.of(properties.resolveProperty("ui.userConfigFolder")
                                         .orElse(Path.of(System.getProperty("user.home"), ".bitnots")
                                                         .toString()))
            Files.createDirectories(folder)
            if (Files.notExists(folder.resolve("ui-settings.properties"))) {
                Files.copy(this.javaClass.getResourceAsStream("/ui-settings.properties"), folder.resolve("ui-settings.properties"))
            }
            addUserConfigToContext()
        }
    }

    private fun addUserConfigToContext() {
        properties.addLocation("file:${Path.of(properties.resolveProperty("ui.userConfigFolder")
                                                       .orElse(Path.of(System.getProperty("user.home"), ".bitnots")
                                                                       .toString()), "ui-settings.properties")}")
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
