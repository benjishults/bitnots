import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.unifier.Substitution
import org.junit.Test
import org.junit.Assert

class SubstitutionTest {

	@Test
	fun substitutionTest() {
		var s1 = Substitution(FV("x").to(Fun("f", Fun("a"))), FV("y").to(Fun("g", Fun("b"), FV("z"))), FV("z").to(FV("x")))
		var s2 = Substitution(FV("x").to(FV("w")), FV("y").to(Fun("h", FV("z"))), FV("z").to(FV("a")))
		var s3 = Substitution(FV("x").to(FV("a")), FV("y").to(Fun("g", Fun("b"), FV("a"))), FV("z").to(FV("w")))
		Assert.assertEquals("", )
	}

}