package com.benjishults.bitnots.parser

import Claim
import com.benjishults.bitnots.model.formulas.propositional.PropositionalFormula
import com.benjishults.bitnots.theory.language.Language
import com.benjishults.bitnots.theory.language.PropositionalLanguage
import java.io.InputStream

class PropositionalFormulaParser(val language: PropositionalLanguage) : Parser<PropositionalFormula> {
    override fun parse(input: InputStream): Iterable<PropositionalFormula> {
        TODO()
    }
}

class ClaimsParser(val language: Language, val formulaParser: PropositionalFormulaParser) : YamlParser(), Parser<Claim> {
    override fun parse(input: InputStream): Iterable<Claim> {
//        mapper.readTree(input).get("claims").asIterable().map { Claim(it.asText()) }
        TODO()
    }
}
