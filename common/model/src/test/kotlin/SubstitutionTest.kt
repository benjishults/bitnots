import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import org.junit.Assert
import org.junit.Test

class SubstitutionTest {

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

	// see if you can get a unification the requires the occurs-check.

}