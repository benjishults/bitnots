package com.benjishults.bitnots.prover.problem

import com.benjishults.bitnots.theory.formula.FormulaForm
import java.time.Instant

class ProblemSet<F: FormulaForm>(
    val name: String,
    val problems: List<ProblemRunDescriptor<F>>
) {

    // treat this like a Stack
    val history: MutableList<ProblemSetRun<F>> = mutableListOf()

    companion object {
        val EMPTY : ProblemSet<*> =
            ProblemSet<FormulaForm>("no problem set selected", emptyList())
    }

}

class ProblemSetRun<F: FormulaForm>(
    val problemSet: ProblemSet<F>,
    val startedAt: Instant,
    vararg val problemRunDescriptors: ProblemRunDescriptor<F>
)
