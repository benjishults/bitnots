package com.benjishults.bitnots.parser

import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.theory.language.Language
import com.benjishults.bitnots.theory.language.PropositionalLanguage
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream

interface Parser<T> {
    fun parse(input: InputStream): Iterable<T>
}

interface ParserSupport<T> {
    fun skipWhitespace(input: InputStream, at: Byte?): Byte?
}

class BitnotsParser<T> : ParserSupport<T> {
    override fun skipWhitespace(input: InputStream, at: Byte?): Byte? {
        if (at?.toChar()?.isWhitespace() ?: false) {
            input.read().let {
                return if (it == -1)
                    null
                else
                    it.toByte()
            }
        }
        return null
    }
}

abstract class YamlParser {
    val mapper = ObjectMapper(YAMLFactory())

    init {
        mapper.registerKotlinModule()
    }
}

interface LanguageParser : Parser<Language> {
}

open class PropositionalLanguageParser : YamlParser(), LanguageParser {

    override fun parse(input: InputStream): Iterable<Language> {
        return listOf(PropositionalLanguage(mapper.readTree(input).get("props").asIterable().map { Prop(it.asText()) }))
    }

}
