package com.benjishults.bitnots.engine

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Sub
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import com.benjishults.bitnots.model.unifier.NotUnifiable

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

    val P = Pred("P", 2)
    val Q = Pred("Q", 2)

    @Before
    fun clearTables() {
        FunctionConstructor.clear()
        FreeVariable.clear()
    }

    @Test
    fun formulaUnificationTest() {
        val sub = Formula.unify(And(P(x, b), Q(y, a)), And(Q(b, a), P(a, b)), EmptySub)
        Assert.assertEquals(sub, Sub(x to a, y to b))
    }

    @Test
    fun substitutionTest() {
        // not idempotent
        var s1 = Sub(
                x to f(y),
                y to z)
        // not idempotent
        // vars occur in keys of s1
        var s2 = Sub(
                x to a,
                y to b,
                z to y)
        // if I allowed non-idempotence or unsafe compositions, I would get the following:
        /* var s3: Sub = Sub(
                x to f(b),
                z to y)*/
        Assert.assertEquals(NotUnifiable, s1 + s2)

        // not idempotent
        s1 = Sub(
                x to f(a),
                y to g(b, z),
                z to x)
        // not idempotent
        // vars occur in keys of s1
        s2 = Sub(
                x to w,
                y to h(z),
                z to a)
/*        s3 = Sub(
                x to f(a),
                y to g(b, a),
                z to w)*/
        Assert.assertEquals(NotUnifiable, s1 + s2)

        // not idempotent
        s1 = Sub(
                x to f(a),
                y to g(b, z),
                z to x)
        // not idempotent
        // vars occur in keys of s1
        s2 = Sub(
                x to w,
                y to h(z),
                z to a)
/*        s3 = Sub(
                x to f(a),
                y to q(b, a),
                z to w)*/
        Assert.assertEquals(NotUnifiable, s1 + s2)
    }

    @Test
    fun unificationTest() {
        val g = Fn("g", 1)
        val p = Fn("p", 3)
        var t1 = p(a, x, f(g(y)))
        var t2 = p(z, f(z), f(u))
        var sigma = Term.unify(t1, t2, EmptySub)

        var mgu = Sub(
                z to a,
                x to f(a),
                u to g(y))

        Assert.assertEquals(t1.applySub(sigma), t2.applySub(sigma))
        Assert.assertEquals(mgu, sigma)

        val h = Fn("h", 2)
        t1 = h(x3, h(x2, x2))
        t2 = h(h(h(x1, x1), x2), x3)

        sigma = Term.unify(t1, t2, EmptySub)
        Assert.assertEquals(t1.applySub(sigma).toString(), t2.applySub(sigma).toString())

        val f = Fn("f", 4)
        t1 = f(x1, this.g(x2, x3), x2, b)
        t2 = f(this.g(h(a, x5), x2), x1, h(a, x4), x4)

        sigma = Term.unify(t1, t2, EmptySub)
        Assert.assertEquals(t1.applySub(sigma), t2.applySub(sigma))
    }

}
