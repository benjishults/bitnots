package com.benjishults.bitnots.parser

class PropositionalFormulaParser() : Parser {
    // override fun parse(input: InputStream): Iterable<PropositionalFormula> {
    //     TODO()
    // }
}

class ClaimsParser(val formulaParser: PropositionalFormulaParser) : YamlParser(), Parser {
//     override fun parse(input: InputStream): Iterable<Claim> {
// //        mapper.readTree(input).get("claims").asIterable().map { Claim(it.asText()) }
//         TODO()
//     }
}
