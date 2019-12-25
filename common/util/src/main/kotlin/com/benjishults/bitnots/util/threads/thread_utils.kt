package com.benjishults.bitnots.util.threads

import java.util.concurrent.atomic.AtomicReference

fun safeSaveAndStartThread(threadReference: AtomicReference<Thread>, thread: Thread) {
    if (threadReference.compareAndSet(null, thread))
        thread.start()
}

fun interruptAndJoin(threadReference: AtomicReference<Thread>) {
    threadReference.get()?.run {
        if (!isInterrupted)
            interrupt()
        join()
    }
}
