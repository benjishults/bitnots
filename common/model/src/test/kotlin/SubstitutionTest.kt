import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import com.benjishults.bitnots.model.terms.FreeVariable

class SubstitutionTest {

	@Before
	fun clearTables() {
		Function.clear()
		FreeVariable.clear()
	}

	@Test
	fun substitutionTest() {
		val x = FV("x")
		val a = Fun("a")
		val y = FV("y")
		val b = Fun("b")
		val z = FV("z")
		val w = FV("w")
		var s1 = Sub(x.to(Fun("f", a)), y.to(Fun("g", b, z)), z.to(x))
		var s2 = Sub(x.to(w), y.to(Fun("h", z)), z.to(a))
		var s3 = Sub(x.to(FV("a")), y.to(Fun("g", b, a)), z.to(w))
		Assert.assertEquals(s1.compose(s2), s3)

		s1 = Sub(x.to(Fun("f", a)), y.to(Fun("g", b, z)), z.to(x))
		s2 = Sub(x.to(w), y.to(Fun("h", z)), z.to(a))
		s3 = Sub(x.to(a), y.to(Fun("g", b, a)), z.to(w))
		Assert.assertEquals("", s1.compose(s2), s3)
	}

	@Test
	fun unificationTest() {
		val x3 = FV("x3")
		val x2 = FV("x2")
		var t1 = Fun("h", x3, Fun("h", x2, x2))
		val x1 = FV("x1")
		var t2 = Fun("h", Fun("h", Fun("h", x1, x1), x2), x3)

		val x4 = FV("x4")
		var mgu = Sub(x2.to(Fun("h", x1, x1)),
				x3.to(Fun("h", Fun("h", x1, x1), x1)),
				x4.to(Fun("h", Fun("h", Fun("h", x1, x1), x1), x1)))

		var sigma = t1.unify(t2)
		println(sigma)

		val b = Fun("b")
		val a = Fun("a")
		val x5 = FV("x5")
		t1 = Fun("f", x1, Fun("g", x2, x3), x2, b)
		t2 = Fun("f", Fun("g", Fun("h", a, x5), x2), x1, Fun("h", a, x4), x4)
		
		val hab = Fun("h", a, b)
		mgu = Sub(x3.to(hab), x4.to(b), x5.to(b), x1.to(Fun("g", hab, hab)), x2.to(hab))
		
		sigma = t1.unify(t2)

	}

	// see if you can get a unification the requires the occurs-check.

}