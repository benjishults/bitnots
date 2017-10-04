package com.benjishults.bitnots.dataIngest

import com.benjishults.bitnots.config.DataManagerConfig
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.theory.problem.Problem
import com.benjishults.bitnots.theory.problem.ProblemSource
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.TptpParser
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.nio.file.Path

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(DataManagerConfig::class))
open class IngestTest {

    @Autowired
    lateinit var data: DataManagerConfig

    @Test
    @Ignore
    fun ingestTest() {
        TptpFileFetcher.findAll(TptpDomain.SYN, TptpFormulaForm.FOF).sortedWith(object : Comparator<Path> {
            override fun compare(o1: Path?, o2: Path?): Int =
                    o1?.getFileName()?.toString()?.compareTo(o2?.getFileName()?.toString() ?: "") ?: 0
        }).forEach { path ->
            TptpParser.parseFile(path).let { tptpFile ->
                tptpFile.inputs.forEach { input ->
                    (input as FolAnnotatedFormula).let { annotated ->
                        data.problemDao().insertAnnotatedFormula(annotated)
                    }
                }
            }
        }
    }

    @Test
    @Ignore
    fun ingestProblemsTest() {
        TptpFileFetcher.findAll(TptpDomain.SYN, TptpFormulaForm.FOF).sortedWith(object : Comparator<Path> {
            override fun compare(o1: Path?, o2: Path?): Int =
                    o1?.getFileName()?.toString()?.compareTo(o2?.getFileName()?.toString() ?: "") ?: 0
        }).forEach { path ->
            val conjectures = mutableListOf<AnnotatedFormula>()
            val axioms = mutableListOf<AnnotatedFormula>()
            val hypotheses = mutableListOf<AnnotatedFormula>()
            TptpParser.parseFile(path).let { tptpFile ->
                tptpFile.inputs.forEach { input ->
                    (input as FolAnnotatedFormula).let { annotated ->
                        when (annotated.formulaRole) {
                            FormulaRoles.conjecture -> {
                                conjectures.add(annotated)
                            }
                            FormulaRoles.hypothesis,
                            FormulaRoles.assumption -> {
                                hypotheses.add(annotated)
                            }
                            FormulaRoles.axiom,
                            FormulaRoles.definition,
                            FormulaRoles.lemma,
                            FormulaRoles.theorem -> {
                                axioms.add(annotated)
                            }
                            FormulaRoles.unknown -> {
                                // ignore
                            }
                            else -> {
                                error("Need to handle ${annotated.formulaRole}.")
                            }
                        }
                    }
                }
            }
            data.problemDao().insertProblem(Problem(conjectures, axioms, hypotheses, ProblemSource.TPTP, path.fileName.toString()))
        }
    }

}
