package com.benjishults.bitnots.regression

import com.benjishults.bitnots.regression.app.FxApplication
import com.benjishults.bitnots.util.threads.safeSaveAndStartThread
import javafx.application.Application
import java.util.concurrent.atomic.AtomicReference

class ProofServiceApplication

object ServiceThread : AtomicReference<Thread>()

object AppThread : AtomicReference<Thread>()

fun main(args: Array<String>) {

    safeSaveAndStartThread(AppThread, Thread {
        Application.launch(FxApplication::class.java, *args)
    })

}
