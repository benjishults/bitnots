import com.benjishults.bitnots.model.formulas.Formula

val MAX_STEPS : Int = 10

data class Claim(val formula: Formula<*>, val provable: Boolean = true, val steps: Int = MAX_STEPS)
