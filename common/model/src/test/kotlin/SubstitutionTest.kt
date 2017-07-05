import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.terms.Function.Const
import com.benjishults.bitnots.model.unifier.Sub
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SubstitutionTest {

    @Before
    fun clearTables() {
        FunctionConstructor.clear()
        FreeVariable.clear()
    }

    @Test
    fun substitutionTest() {
        val a = Const("a")
        val b = Fn("b", 0)
        val h = Fn("h", 1)
        val f = Fn("f", 1)
        val g = Fn("g, 2")
        val q = Fn("q, 2")
        val w = FV("w")
        val x = FV("x")
        val y = FV("y")
        val z = FV("z")
        var s1 = Sub(x.to(f(a)), y.to(g(b, z)), z.to(x))
        var s2 = Sub(x.to(w), y.to(h(z)), z.to(a))
        var s3 = Sub(x.to(f(a)), y.to(q(b, a)), z.to(w))
        Assert.assertEquals(s1.compose(s2), s3)
    }

    @Test
    fun unificationTest() {
        val x3 = FV("x3")
        val x2 = FV("x2")
        var t1 = Fun("h", arrayOf(x3, Fun("h", arrayOf(x2, x2))))
        val x1 = FV("x1")
        var t2 = Fun("h", arrayOf(Fun("h", arrayOf(Fun("h", arrayOf(x1, x1)), x2)), x3))

        val x4 = FV("x4")
        var mgu = Sub(x2.to(Fun("h", arrayOf(x1, x1))),
                x3.to(Fun("h", arrayOf(Fun("h", arrayOf(x1, x1)), x1))),
                x4.to(Fun("h", arrayOf(Fun("h", arrayOf(Fun("h", arrayOf(x1, x1)), x1)), x1))))

        var sigma = t1.linearUnify(t2)
        println(sigma)

        val b = Fun("b")
        val a = Fun("a")
        val x5 = FV("x5")
        t1 = Fun("f", arrayOf(x1, Fun("g", arrayOf(x2, x3)), x2, b))
        t2 = Fun("f", arrayOf(Fun("g", arrayOf(Fun("h", arrayOf(a, x5)), x2)), x1, Fun("h", arrayOf(a, x4)), x4))

        val hab = Fun("h", arrayOf(a, b))
        mgu = Sub(x3.to(hab), x4.to(b), x5.to(b), x1.to(Fun("g", arrayOf(hab, hab))), x2.to(hab));

        sigma = t1.unify(t2)


    }
    // see if you can get a unification the requires the occurs-check.

}