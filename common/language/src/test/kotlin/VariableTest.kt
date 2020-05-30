import com.benjishults.bitnots.model.terms.FreeVariable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VariableTest {

    @Test
    fun testNewVariables() {
        listOf("a", "b", "c", "d", "e", "f", "b-line", "c_").map {
            FreeVariable.newSimilar(it)
        }.forEach { name ->
            Assertions.assertEquals(name.cons.name + "_0", FreeVariable.newSimilar(name.cons.name).cons.name)
            Assertions.assertEquals(name.cons.name + "_1", FreeVariable.newSimilar(name.cons.name).cons.name)
        }
    }

    @Test
    fun testNumericSuffixedVariables() {
        FreeVariable.newSimilar("a_2")
        Assertions.assertEquals("a_3", FreeVariable.newSimilar("a_2").cons.name)
    }

}
