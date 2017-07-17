import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.SystemOfMultiEquations
import com.benjishults.bitnots.model.unifier.linearUnify
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class SubstitutionTest {

    val a by lazy { Const("a") }
    val b by lazy { Const("b") }

    val h by lazy { Fn("h", 1) }
    val f by lazy { Fn("f", 1) }

    val g by lazy { Fn("g", 2) }
    val q by lazy { Fn("q", 2) }

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
                x to f(a),
                y to g(b, z),
                z to x)
        var s2 = Sub(
                x to w,
                y to h(z),
                z to a)
        var s3 = Sub(
                x to f(a),
                y to q(b, a),
                z to w)
        Assert.assertEquals(s1.compose(s2), s3)
    }

    @Test
    fun disagreementTest() {
        val f = Fn("f", 4)
        val h = Fn("h", 2)
        var t1 = h(x3, h(x2, x2))
        var t2 = h(h(h(x1, x1), x2), x3)

        println(SystemOfMultiEquations.disagreementSet(t1, t2))

        t1 = f(x1, g(x2, x3), x2, b)
        t2 = f(g(h(a, x5), x2), x1, h(a, x4), x4)

        println(SystemOfMultiEquations.disagreementSet(t1, t2))

    }

    @Test
    @Ignore
    fun unificationTest() {
        val h = Fn("h", 2)
        var t1 = h(x3, h(x2, x2))
        var t2 = h(h(h(x1, x1), x2), x3)

        var mgu = Sub(
                x2 to h(x1, x1),
                x3 to h(h(x1, x1), x1),
                x4 to h(h(h(x1, x1), x1), x1))

        var sigma = t1.linearUnify(t2)
        println(sigma)

        val f = Fn("f", 4)
        t1 = f(x1, g(x2, x3), x2, b)
        t2 = f(g(h(a, x5), x2), x1, h(a, x4), x4)

        val hab = h(a, b)
        mgu = Sub(
                x3 to hab,
                x4 to b,
                x5 to b,
                x1 to g(hab, hab),
                x2 to hab);

        sigma = t1.unify(t2)

    }
// see if you can get a unification the requires the occurs-check.


}