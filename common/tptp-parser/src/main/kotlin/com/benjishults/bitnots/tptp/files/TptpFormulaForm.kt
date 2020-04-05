package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.theory.formula.FormulaForm

enum class TptpFormulaForm(val form: Char) : FormulaForm {
    CNF('-') {
        // override fun prove(problemDescriptor: ProblemDescriptor) {
        //     TODO("Not yet implemented")
        // }
    },
    FOF('+') {
        // override fun prove(problemDescriptor: ProblemDescriptor) {
        //     val harness = when (problemDescriptor.form) {
        //         TptpFormulaForm.FOF -> FolTableauHarness(version = "${gitCommitTime()}__${gitCommitId()}")
        //         TptpFormulaForm.CNF -> PropositionalTableauHarness(version = "${gitCommitTime()}__${gitCommitId()}")
        //         else                -> error("unsupported formula form")
        //     }
        //     val prover = harness.toProver()
        //     val descriptor = TptpProblemFileDescriptor(problemDescriptor)
        //     TptpFormulaClassifier().classify(
        //         // TODO simplify these APIs
        //         when (problemDescriptor.form) {
        //             TptpFormulaForm.FOF -> TptpFofParser
        //             TptpFormulaForm.CNF -> TptpCnfParser
        //             else                -> error("unsupported formula form")
        //         }.parseFile(TptpFileFetcher.findProblemFile(descriptor))
        //     ).let { (hyps, targets) ->
        //         // clearInternTables()
        //         val hypothesis = hyps.toConjunct()
        //         targets.forEach { target ->
        //             val proofInProgress = FolTableau(
        //                 hypothesis?.let {
        //                     Implies(it, target)
        //                 } ?: target)
        //             prover.limitedTimeProve(
        //                 proofInProgress as Nothing,
        //                 -1L
        //                 // problemDescriptor.timeLimit
        //             ).indicator.isDone()
        //             // TODO create new runs and update table
        //         }
        //     }
        //
        // }
    },
    TFF('_') {
        // override fun prove(problemDescriptor: ProblemDescriptor) {
        //     TODO("Not yet implemented")
        // }
    },
    TFF_WITH_ARITHMETIC('=') {
        // override fun prove(problemDescriptor: ProblemDescriptor) {
        //     TODO("Not yet implemented")
        // }
    },
    THF('^') {
        // override fun prove(problemDescriptor: ProblemDescriptor) {
        //     TODO("Not yet implemented")
        // }
    };

    companion object {
        fun findByForm(form: Char) = values().find { it.form == form } ?: error("Malformed formula form: '$form'.")
    }

}
