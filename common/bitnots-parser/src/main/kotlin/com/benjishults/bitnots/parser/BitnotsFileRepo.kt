package com.benjishults.bitnots.parser

import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula

object BitnotsFileRepo: ProblemSource<FolAnnotatedFormula, Tokenizer> {
    override val abbreviation: String = "bitnots"
    override val parser: Parser<FolAnnotatedFormula, Tokenizer> =
}
