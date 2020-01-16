import com.benjishults.bitnots.model.terms.FreeVariable
import org.junit.Test

class VariableTest {
    @Test
    fun testNewVariables() {
        val names = listOf("a", "b", "c", "d", "e", "f", "a-2", "b-line", "c-")
        val vars = names.map {
            FreeVariable.new(it)
        }.toMutableList()
        for (name in names) {
            vars.add(FreeVariable.new(name))
        }
        println(vars)
    }
}
