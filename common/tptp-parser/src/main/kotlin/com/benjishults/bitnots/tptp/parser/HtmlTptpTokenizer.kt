package com.benjishults.bitnots.tptp.parser

import java.io.BufferedReader

class HtmlTptpTokenizer(reader: BufferedReader, fileName: String) : TptpTokenizer(reader, fileName) {

    // This will move the cursor (if necessary) so that peekChar is a non-whitespace.  Only works after the first call to popChar().
    override fun skipWhitespace() {
        while (peekChar != -1) {
            if (atStartOfLine && peekChar.toChar() == '%') {
                nextLine()
            } else if (peekChar.toChar() == '<') {
                popChar()
                if (peekChar.toChar() in arrayOf('a','A')) {
                    popChar()
                }
            } else if (!peekChar.toChar().isWhitespace())
                return
            else  {
                popChar()
            }
        }
    }

    // private fun skipHtml



}
