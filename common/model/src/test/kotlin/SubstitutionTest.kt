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
		var s1 = Sub(FV("x").to(Fun("f", Fun("a"))), FV("y").to(Fun("g", Fun("b"), FV("z"))), FV("z").to(FV("x")))
		var s2 = Sub(FV("x").to(FV("w")), FV("y").to(Fun("h", FV("z"))), FV("z").to(FV("a")))
		var s3 = Sub(FV("x").to(FV("a")), FV("y").to(Fun("g", Fun("b"), FV("a"))), FV("z").to(FV("w")))
		Assert.assertEquals("", s1.compose(s2), s3)

		s1 = Sub(FV("x").to(Fun("f", Fun("a"))), FV("y").to(Fun("g", Fun("b"), FV("z"))), FV("z").to(FV("x")))
		s2 = Sub(FV("x").to(FV("w")), FV("y").to(Fun("h", FV("z"))), FV("z").to(FV("a")))
		s3 = Sub(FV("x").to(FV("a")), FV("y").to(Fun("g", Fun("b"), FV("a"))), FV("z").to(FV("w")))
		Assert.assertEquals("", s1.compose(s2), s3)
	}

	@Test
	fun unificationTest() {
		var t1 = Fun("h", FV("x3"), Fun("h", FV("x2"), FV("x2")))
		var t2 = Fun("h", Fun("h", Fun("h", FV("x1"), FV("x1")), FV("x2")), FV("x3"))

		var mgu = Sub(FV("x2").to(Fun("h", FV("x1"), FV("x1"))),
				FV("x3").to(Fun("h", Fun("h", FV("x1"), FV("x1")), FV("x1"))),
				FV("x4").to(Fun("h", Fun("h", Fun("h", FV("x1"), FV("x1")), FV("x1")), FV("x1"))))

		var sigma = t1.unify(t2)
		println(sigma)
	}

	// see if you can get a unification the requires the occurs-check.

}