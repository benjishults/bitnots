package com.benjishults.bitnots.proofService

import com.benjishults.bitnots.proofService.app.ProofApplication
import com.benjishults.bitnots.proofService.control.ProofKickoff
import com.benjishults.bitnots.util.threads.interruptAndJoin
import com.benjishults.bitnots.util.threads.safeSaveAndStartThread
import javafx.application.Application
import org.apache.camel.main.BaseMainSupport
import org.apache.camel.main.MainListenerSupport
import org.apache.camel.spring.javaconfig.Main
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.concurrent.atomic.AtomicReference

@Configuration
@Import(ProofKickoff::class)
class ProofServiceApplication

object ServiceThread : AtomicReference<Thread>()

object AppThread : AtomicReference<Thread>()

fun main(args: Array<String>) {
    // start Camel routes in another thread
    val main = Main().apply {
        addMainListener(object : MainListenerSupport() {
            override fun beforeStart(main: BaseMainSupport?) {
                // start FX application
                safeSaveAndStartThread(AppThread, Thread {
                    Application.launch(ProofApplication::class.java, *args)
                })
                super.beforeStart(main)
            }

            override fun beforeStop(main: BaseMainSupport?) {
                ServiceThread.set(null)
                interruptAndJoin(AppThread)
                super.beforeStop(main)
            }

        })
        setConfigClass(ProofServiceApplication::class.java)
        safeSaveAndStartThread(ServiceThread, Thread {
            run(args)
        })
    }
}
