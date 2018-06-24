package com.benjishults.bitnots.model.util

import java.util.concurrent.ConcurrentHashMap

fun <P1, P2, R> ((P1, P2) -> R).memoize(): (P1, P2) -> R {
    return object : (P1, P2) -> R {
        private val m = MemoizedHandler<((P1, P2) -> R), MemoizeKey2<P1, P2, R>, R>(this@memoize)
        override fun invoke(p1: P1, p2: P2) = m(MemoizeKey2(p1, p2))
    }
}

fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoize(): (P1, P2, P3) -> R {
    return object : (P1, P2, P3) -> R {
        private val m = MemoizedHandler<((P1, P2, P3) -> R), MemoizeKey3<P1, P2, P3, R>, R>(this@memoize)
        override fun invoke(p1: P1, p2: P2, p3: P3) = m(MemoizeKey3(p1, p2, p3))
    }
}

private interface MemoizedCall<in F, out R> {
    operator fun invoke(f: F): R
}

private data class MemoizeKey2<out P1, out P2, R>(val p1: P1, val p2: P2) : MemoizedCall<(P1, P2) -> R, R> {
    override fun invoke(f: (P1, P2) -> R) = f(p1, p2)
}

private data class MemoizeKey3<out P1, out P2, out P3, R>(val p1: P1, val p2: P2, val p3: P3) : MemoizedCall<(P1, P2, P3) -> R, R> {
    override fun invoke(f: (P1, P2, P3) -> R) = f(p1, p2, p3)
}

private class MemoizedHandler<F, in K : MemoizedCall<F, R>, out R>(val f: F) {
    // TODO probably don't need concurrency protection here.
    private val m = ConcurrentHashMap<K, R>()
    operator fun invoke(k: K): R {
        return m[k] ?: run {
            val r = k(f)
            m.putIfAbsent(k, r)
            r
        }
    }
}
