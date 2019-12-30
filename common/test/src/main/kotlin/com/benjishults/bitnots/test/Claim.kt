package com.benjishults.bitnots.test

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.Tableau
import org.junit.Assert

val DEFAULT_MAX_STEPS: Int = 30

class ProofConstraints(val minSteps: Int, val maxSteps: Int = minSteps)

sealed class Claim(
        val formula: Formula<*>
) {
    abstract fun validate(tableau: Tableau)
}

class FalseClaim(
        formula: Formula<*>
) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        println("WARN: in some logics, this could run forever.")
        while (true) {
            if (tableau.findCloser().isCloser())
                Assert.fail("Unexpectedly proved ${formula}.")
            else if (!tableau.step())
                break
        }
        if (tableau.findCloser().isCloser()) {
            Assert.fail("Unexpectedly proved ${formula}.")
        }
    }
}

class TrueClaim(
        formula: Formula<*>,
        val steps: ProofConstraints = ProofConstraints(0, DEFAULT_MAX_STEPS)
) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        for (step in steps.maxSteps downTo 1) {
            if (tableau.findCloser().isCloser()) {
                (steps.maxSteps - step).takeIf {
                    it < steps.minSteps
                }?.let {
                    error("${formula} is unexpectedly proved before ${it + 1} steps.")
                }
            }
            tableau.step()
        }
        if (!tableau.findCloser().isCloser()) {
            error("Failed to prove ${formula} with ${steps.maxSteps} steps.")
        }

    }
}

class Conjecture(formula: Formula<*>) : Claim(formula) {
    override fun validate(tableau: Tableau) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
