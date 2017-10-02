package com.benjishults.bitnots.dataIngest

import com.benjishults.bitnots.config.DataManagerConfig
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.TptpParser
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
}

//                        when (annotated.formulaRole) {
//                            FormulaRoles.axiom,
//                            FormulaRoles.hypothesis,
//                            FormulaRoles.assumption,
//                            FormulaRoles.definition,
//                            FormulaRoles.theorem,
//                            FormulaRoles.corollary,
//                            FormulaRoles.lemma -> {
//                                hyps.add(annotated.formula)
//                            }
//
//                            FormulaRoles.conjecture -> {
//                                targets.add(annotated.formula)
//                            }
//                            FormulaRoles.negated_conjecture -> {
//                                targets.add(Not(annotated.formula))
//                            }
//
//                            FormulaRoles.fi_domain,
//                            FormulaRoles.fi_functors,
//                            FormulaRoles.fi_predicates,
//                            FormulaRoles.plain,
//                            FormulaRoles.type -> {
//                                error("Don't know what to do with ${annotated.formulaRole}.")
//                            }
//                            FormulaRoles.unknown -> error("Unknown role found.")
//                        }

