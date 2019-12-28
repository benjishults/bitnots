package com.benjishults.bitnots.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream

interface Parser {
    // fun parse(input: InputStream): Iterable<T>
}

interface ParserSupport<T> {
    fun skipWhitespace(input: InputStream, at: Byte?): Byte?
}

class BitnotsParser<T> : ParserSupport<T> {
    override fun skipWhitespace(input: InputStream, at: Byte?): Byte? {
        if (at?.toChar()?.isWhitespace() == true) {
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

interface LanguageParser : Parser
