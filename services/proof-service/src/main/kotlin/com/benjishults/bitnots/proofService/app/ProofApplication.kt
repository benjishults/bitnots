package com.benjishults.bitnots.proofService.app

import com.benjishults.bitnots.proofService.AppThread
import com.benjishults.bitnots.proofService.ServiceThread
import com.benjishults.bitnots.util.threads.interruptAndJoin
import tornadofx.*

class ProofApplication : App(TheoryLoader::class) {

    /**
     * Stop the service thread first.
     */
    override fun stop() {
        // TODO give warning message in UI
        AppThread.set(null)
        interruptAndJoin(ServiceThread)
        super.stop()
    }

}
