package com.benjishults.bitnots.test

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.Tableau

val DEFAULT_MAX_STEPS: Int = 30

data class ClosedInterval(val min: Int, val max: Int = min)

sealed class Claim(
        val formula: Formula<*>
) {
    abstract fun validate(tableau: Tableau)
}

class FalseClaim(
        formula: Formula<*>
) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class TrueClaim(
        formula: Formula<*>,
        val steps: ClosedInterval = ClosedInterval(0, DEFAULT_MAX_STEPS)
) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        for (step in steps.max downTo 1) {
            if (tableau.findCloser().isCloser()) {
                (steps.max - step).takeIf {
                    it < steps.min
                }?.let {
                    error("${formula} is unexpectedly proved before ${it + 1} steps.")
                }
            }
            tableau.step()
        }
        if (!tableau.findCloser().isCloser()) {
            error("Failed to prove ${formula} with ${steps.max} steps.")
        }

    }
}

class Conjecture(formula: Formula<*>) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
