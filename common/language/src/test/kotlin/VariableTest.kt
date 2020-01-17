import com.benjishults.bitnots.model.terms.FreeVariable
import org.junit.Assert
import org.junit.Test

class VariableTest {

    @Test
    fun testNewVariables() {
        listOf("a", "b", "c", "d", "e", "f", "b-line", "c-").map {
            FreeVariable.new(it)
        }.forEach { name ->
            Assert.assertEquals(name.cons.name + "-0", FreeVariable.new(name.cons.name).cons.name)
            Assert.assertEquals(name.cons.name + "-1", FreeVariable.new(name.cons.name).cons.name)
        }
    }

    @Test
    fun testNumericSuffixedVariables() {
        FreeVariable.new("a-2")
        Assert.assertEquals("a-3", FreeVariable.new("a-2").cons.name)
    }

}
