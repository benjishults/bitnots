import com.benjishults.bitnots.model.terms.FreeVariable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VariableTest {

    @Test
    fun testNewVariables() {
        listOf("a", "b", "c", "d", "e", "f", "b-line", "c-").map {
            FreeVariable.new(it)
        }.forEach { name ->
            Assertions.assertEquals(name.cons.name + "-0", FreeVariable.new(name.cons.name).cons.name)
            Assertions.assertEquals(name.cons.name + "-1", FreeVariable.new(name.cons.name).cons.name)
        }
    }

    @Test
    fun testNumericSuffixedVariables() {
        FreeVariable.new("a-2")
        Assertions.assertEquals("a-3", FreeVariable.new("a-2").cons.name)
    }

}
