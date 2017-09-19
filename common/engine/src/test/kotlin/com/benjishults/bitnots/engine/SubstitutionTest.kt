package com.benjishults.bitnots.engine

import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.Sub
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SubstitutionTest {

    val a by lazy { Const("a") }
    val b by lazy { Const("b") }

    val h by lazy { Fn("h", 1) }
    val f by lazy { Fn("f", 1) }

    val g by lazy { Fn("g", 2) }
    val q by lazy { Fn("q", 2) }

    val u by lazy { FV("u") }
    val w by lazy { FV("w") }
    val x by lazy { FV("x") }
    val y by lazy { FV("y") }
    val z by lazy { FV("z") }

    val x1 by lazy { FV("x1") }
    val x2 by lazy { FV("x2") }
    val x3 by lazy { FV("x3") }
    val x4 by lazy { FV("x4") }
    val x5 by lazy { FV("x5") }

    @Before
    fun clearTables() {
        FunctionConstructor.clear()
        FreeVariable.clear()
    }

    @Test
    fun substitutionTest() {
        var s1 = Sub(
                x to f(y),
                y to z)
        var s2 = Sub(
                x to a,
                y to b,
                z to y)
        var s3 = Sub(
                x to f(b),
                z to y)
        Assert.assertEquals(s3, s1 + s2)

        s1 = Sub(
                x to f(a),
                y to g(b, z),
                z to x)
        s2 = Sub(
                x to w,
                y to h(z),
                z to a)
        s3 = Sub(
                x to f(a),
                y to q(b, a),
                z to w)
        Assert.assertEquals(s3, s1 + s2)
    }

    @Test
    fun unificationTest() {
        val g = Fn("g", 1)
        val p = Fn("p", 3)
        var t1 = p(a, x, f(g(y)))
        var t2 = p(z, f(z), f(u))
        var sigma = t1.unify(t2)

        var mgu = Sub(
                z to a,
                x to f(a),
                u to g(y))
        
        Assert.assertEquals(t1.applySub(sigma), t2.applySub(sigma))
        Assert.assertEquals(mgu, sigma)

        val h = Fn("h", 2)
        t1 = h(x3, h(x2, x2))
        t2 = h(h(h(x1, x1), x2), x3)

        sigma = t1.unify(t2)
        Assert.assertEquals(t1.applySub(sigma).toString(), t2.applySub(sigma).toString())

        val f = Fn("f", 4)
        t1 = f(x1, this.g(x2, x3), x2, b)
        t2 = f(this.g(h(a, x5), x2), x1, h(a, x4), x4)

        sigma = t1.unify(t2)
        Assert.assertEquals(t1.applySub(sigma), t2.applySub(sigma))
    }

}
